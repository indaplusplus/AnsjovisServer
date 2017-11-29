package com.paul;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.CryptoPrimitive;

public class Main {

  public static void main(String[] args) {
    try {
      ServerSocket serverSocket = new ServerSocket(0xDAD);
      Socket socket = serverSocket.accept();

      new ConnectionHandler(socket).start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
