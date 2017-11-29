package com.paul;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class AngelaMerkelTree {

  public ArrayList<Byte[]> blocks = new ArrayList<>();
  public ArrayList<ArrayList<Block>> blockLevels = new ArrayList<>();

  public void addBlock(byte[] bytes) {
    blocks.add(toByteObjectArray(bytes));
  }

  public void generate() {
    HashHandler hashHandler = new HashHandler();

    ArrayList<Block> startingBlocks = new ArrayList<>();

    for (Byte[] bytes : blocks) {
      Block b  = new Block(0, hashHandler.hash(toByteArray(bytes)), null, null);
      startingBlocks.add(b);
    }

    blockLevels.add(startingBlocks);

    for (int i = 0; true; i++) {
      ArrayList<Block> nextBlockLevel = new ArrayList<>();

      Block blockPair = null;
      for (Block block : blockLevels.get(i)) {
        if (blockPair == null) {
          blockPair = block;
        } else {
          Block newBlock = new Block(i + 1,
              hashHandler.hash(mergeByteArrays(blockPair.value, block.value)),
              blockPair, block);
          nextBlockLevel.add(newBlock);
          blockPair = null;
        }
      }

      blockLevels.add(nextBlockLevel);

      if (nextBlockLevel.size() == 1) {
        break;
      }
    }
  }

  private byte[] mergeByteArrays(byte[] one, byte[] two) {
//    byte[] byteArray = new byte[one.length + two.length];
    byte[] byteArray = new byte[one.length];

    for (int i = 0; i < one.length; i++) {
      byteArray[i] = new Integer(one[i]^two[i]).byteValue();
    }

//    for (int i = 0; i < one.length; i++) {
//      byteArray[i] = one[i];
//    }
//
//    for (int i = 0; i < two.length; i++) {
//      byteArray[i] = two[i];
//    }

    return byteArray;
  }

  private Byte[] toByteObjectArray(byte[] bytes) {
    Byte[] byteObjectArray = new Byte[bytes.length];

    for (int i = 0; i < bytes.length; i++) {
      byteObjectArray[i] = bytes[i];
    }

    return byteObjectArray;
  }

  public byte[] toByteArray(Byte[] bytes) {
    byte[] byteArray = new byte[bytes.length];

    for (int i = 0; i < bytes.length; i++) {
      byteArray[i] = bytes[i];
    }

    return byteArray;
  }

  public class Block {

    public Block(int level, byte[] value, Block parentOne, Block parentTwo) {
      this.level = level;
      this.value = value;
      this.parentOne = parentOne;
      this.parentTwo = parentTwo;
    }

    public int level;
    public byte[] value;
    public Block parentOne;
    public Block parentTwo;
  }
}
