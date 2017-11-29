package com.paul;

import com.google.protobuf.ByteString;
import com.paul.AnsjovisOuterClass.Ansjovis;
import com.paul.AnsjovisOuterClass.Ansjovis.Action;
import java.io.IOException;
import java.net.Socket;

public class ConnectionHandler extends Thread {

  private Socket socket;
  private byte[] receivedData = new byte[] {};
  private AngelaMerkelTree angelaMerkelTree;

  public ConnectionHandler(Socket socket) {
    this.socket = socket;
    this.angelaMerkelTree = new AngelaMerkelTree();
  }

  @Override
  public void run() {
    CryptHandler cryptHandler = new CryptHandler();

    try {
      while (socket.isConnected()) {
        Ansjovis message = receive();

        System.out.println(message.getAction() + ": " + message.getMessage());

        switch (message.getAction()) {
          case HELLO:
            send(Action.KEY, cryptHandler.getPublicKey().getEncoded());
            break;
          case TRANSFER:
            byte[] decrypt = cryptHandler.decrypt(message.getMessage().toByteArray());

            appendBytes(decrypt);
            angelaMerkelTree.addBlock(decrypt);

            break;
          case END:
            angelaMerkelTree.generate();
            sendDataAndHashes(message.getMessage().toByteArray()[0]);
            socket.close();

            return;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void appendBytes(byte[] bytes) {
    byte[] update = new byte[receivedData.length + bytes.length];

    for (int i = 0; i < receivedData.length; i++) {
      update[i] = receivedData[i];
    }

    for (int i = 0; i < bytes.length; i++) {
      update[receivedData.length + i] = bytes[i];
    }

    receivedData = update;
  }

  public void sendDataAndHashes(int block) throws IOException {
    send(Action.END, angelaMerkelTree.toByteArray(angelaMerkelTree.blocks.get(block)));

    int level = 0;
    int index = block;

    while (level < angelaMerkelTree.blockLevels.size()) {
      int opposite;

      if (level == angelaMerkelTree.blockLevels.size() - 1) {
        index = 0;
      }

      if (angelaMerkelTree.blockLevels.get(level).size() == 1) {
        opposite = 0;
      } else if (index % 2 == 0) {
        opposite = index + 1;
      } else {
        opposite = index - 1;
      }

      send(Action.END, angelaMerkelTree.blockLevels.get(level).get(opposite).value);

      index = (int) Math.round(((double) index + 1D) / 2D) - 1;

      level++;
    }

    send(Action.END, new byte[] {});
  }

  public void send(Ansjovis.Action action, byte[] message) throws IOException {
    Ansjovis.Builder builder = Ansjovis.newBuilder();
    builder.setAction(action);
    builder.setMessage(ByteString.copyFrom(message));

    builder.build().writeDelimitedTo(socket.getOutputStream());

    socket.getOutputStream().flush();
  }

  public Ansjovis receive() throws IOException {
    return Ansjovis.parseDelimitedFrom(socket.getInputStream());
  }
}
