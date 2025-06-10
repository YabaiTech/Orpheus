package org.orpheus;

import java.util.ArrayList;
import java.util.List;

class Compress {
  private ArrayList<Byte> normalBits;
  private ArrayList<Byte> exceptionalBits;

  // this is basically a placeholder. We initialize the whole thing through the
  // `compress()` method call
  Compress(int normalBitsLength, int exceptionalBitsLength) {
    normalBits = null;
    exceptionalBits = null;
  }

  public void processSubFingerprint(int elem) {
    // I've kept the name of the local variable and the class's data member the same
    // for now to keep the Java port as close to the Zig code as we can.
    //
    // However, this is quite confusing. That's why we might want to change it later
    // on during the refactoring phase.
    final byte normalBits = 3;
    final byte maxNormalValue = (1 << normalBits) - 1;

    int bit = 1;
    int lastBit = 0;
    int x = elem;

    // WARNING: The adding of new values into the following ArrayList(s) might throw
    // an error if the we ran out of memory. We might want to handle that in a
    // user-friendly manner (through the UI)
    while (x != 0) {
      if ((x & 1) != 0) {
        final byte value = (byte) (bit - lastBit);

        if (value >= maxNormalValue) {
          this.normalBits.add(maxNormalValue);
          this.exceptionalBits.add((byte) (value - maxNormalValue));
        } else {
          this.normalBits.add(value);
        }
        lastBit = bit;
      }
      x >>>= 1;
      bit += 1;
    }
    this.normalBits.add((byte) 0);
  }

  public byte[] compress(int[] fingerprint) {
    if (fingerprint.length > 0) {
      // WARNING: Any one of the following 3 lines can throw. Handle it gracefully
      this.normalBits = new ArrayList<>(fingerprint.length);
      this.exceptionalBits = new ArrayList<>(fingerprint.length / 10);
      processSubFingerprint(fingerprint[0]);

      for (int i = 1; i < fingerprint.length; i++) {
        int diff = fingerprint[i] ^ fingerprint[i - 1];
        processSubFingerprint(diff);
      }
    }

    // Not sure about the following Java port (upto the end of this method)
    int normalBitsSize = packedIntArraySize(3, normalBits.size());
    int exceptionalBitsSize = packedIntArraySize(5, exceptionalBits.size());
    byte[] result = new byte[4 + normalBitsSize + exceptionalBitsSize];

    int algorithm = 1;
    result[0] = (byte) algorithm;
    result[1] = (byte) (fingerprint.length >> 16);
    result[2] = (byte) (fingerprint.length >> 8);
    result[3] = (byte) (fingerprint.length);

    int ptr = 4;
    ptr = packInt3Array(result, ptr, this.normalBits);
    ptr = packInt5Array(result, ptr, this.exceptionalBits);

    return result;
  }

  public int packedIntArraySize(int n, int size) {
    return (size * n + 7) / 8;
  }

  // Not sure about the following Java port
  private int packInt3Array(byte[] dest, int offset, List<Byte> src) {
    int srcIndex = 0;
    int destIndex = offset;

    while (srcIndex + 7 < src.size()) {
      dest[destIndex++] = (byte) ((src.get(srcIndex) & 0x07) |
          ((src.get(srcIndex + 1) & 0x07) << 3) |
          ((src.get(srcIndex + 2) & 0x03) << 6));
      dest[destIndex++] = (byte) (((src.get(srcIndex + 2) & 0x04) >> 2) |
          ((src.get(srcIndex + 3) & 0x07) << 1) |
          ((src.get(srcIndex + 4) & 0x07) << 4) |
          ((src.get(srcIndex + 5) & 0x01) << 7));
      dest[destIndex++] = (byte) (((src.get(srcIndex + 5) & 0x06) >> 1) |
          ((src.get(srcIndex + 6) & 0x07) << 2) |
          ((src.get(srcIndex + 7) & 0x07) << 5));
      srcIndex += 8;
    }

    int remaining = src.size() - srcIndex;
    switch (remaining) {
      case 7:
        dest[destIndex++] = (byte) ((src.get(srcIndex) & 0x07) |
            ((src.get(srcIndex + 1) & 0x07) << 3) |
            ((src.get(srcIndex + 2) & 0x03) << 6));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 2) & 0x04) >> 2) |
            ((src.get(srcIndex + 3) & 0x07) << 1) |
            ((src.get(srcIndex + 4) & 0x07) << 4) |
            ((src.get(srcIndex + 5) & 0x01) << 7));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 5) & 0x06) >> 1) |
            ((src.get(srcIndex + 6) & 0x07) << 2));
        break;
      case 6:
        dest[destIndex++] = (byte) ((src.get(srcIndex) & 0x07) |
            ((src.get(srcIndex + 1) & 0x07) << 3) |
            ((src.get(srcIndex + 2) & 0x03) << 6));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 2) & 0x04) >> 2) |
            ((src.get(srcIndex + 3) & 0x07) << 1) |
            ((src.get(srcIndex + 4) & 0x07) << 4) |
            ((src.get(srcIndex + 5) & 0x01) << 7));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 5) & 0x06) >> 1));
        break;
      case 5:
        dest[destIndex++] = (byte) ((src.get(srcIndex) & 0x07) |
            ((src.get(srcIndex + 1) & 0x07) << 3) |
            ((src.get(srcIndex + 2) & 0x03) << 6));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 2) & 0x04) >> 2) |
            ((src.get(srcIndex + 3) & 0x07) << 1) |
            ((src.get(srcIndex + 4) & 0x07) << 4));
        break;
      case 4:
        dest[destIndex++] = (byte) ((src.get(srcIndex) & 0x07) |
            ((src.get(srcIndex + 1) & 0x07) << 3) |
            ((src.get(srcIndex + 2) & 0x03) << 6));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 2) & 0x04) >> 2) |
            ((src.get(srcIndex + 3) & 0x07) << 1));
        break;
      case 3:
        dest[destIndex++] = (byte) ((src.get(srcIndex) & 0x07) |
            ((src.get(srcIndex + 1) & 0x07) << 3) |
            ((src.get(srcIndex + 2) & 0x03) << 6));
        dest[destIndex++] = (byte) ((src.get(srcIndex + 2) & 0x04) >> 2);
        break;
      case 2:
        dest[destIndex++] = (byte) ((src.get(srcIndex) & 0x07) |
            ((src.get(srcIndex + 1) & 0x07) << 3));
        break;
      case 1:
        dest[destIndex++] = (byte) (src.get(srcIndex) & 0x07);
        break;
    }
    return destIndex;
  }

  // Not sure about the following Java port
  private int packInt5Array(byte[] dest, int offset, List<Byte> src) {
    int srcIndex = 0;
    int destIndex = offset;

    while (srcIndex + 7 < src.size()) {
      dest[destIndex++] = (byte) ((src.get(srcIndex) & 0x1f) |
          ((src.get(srcIndex + 1) & 0x07) << 5));
      dest[destIndex++] = (byte) (((src.get(srcIndex + 1) & 0x18) >> 3) |
          ((src.get(srcIndex + 2) & 0x1f) << 2) |
          ((src.get(srcIndex + 3) & 0x01) << 7));
      dest[destIndex++] = (byte) (((src.get(srcIndex + 3) & 0x1e) >> 1) |
          ((src.get(srcIndex + 4) & 0x0f) << 4));
      dest[destIndex++] = (byte) (((src.get(srcIndex + 4) & 0x10) >> 4) |
          ((src.get(srcIndex + 5) & 0x1f) << 1) |
          ((src.get(srcIndex + 6) & 0x03) << 6));
      dest[destIndex++] = (byte) (((src.get(srcIndex + 6) & 0x1c) >> 2) |
          ((src.get(srcIndex + 7) & 0x1f) << 3));
      srcIndex += 8;
    }

    int remaining = src.size() - srcIndex;
    switch (remaining) {
      case 7:
        dest[destIndex++] = (byte) ((src.get(srcIndex) & 0x1f) |
            ((src.get(srcIndex + 1) & 0x07) << 5));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 1) & 0x18) >> 3) |
            ((src.get(srcIndex + 2) & 0x1f) << 2) |
            ((src.get(srcIndex + 3) & 0x01) << 7));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 3) & 0x1e) >> 1) |
            ((src.get(srcIndex + 4) & 0x0f) << 4));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 4) & 0x10) >> 4) |
            ((src.get(srcIndex + 5) & 0x1f) << 1) |
            ((src.get(srcIndex + 6) & 0x03) << 6));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 6) & 0x1c) >> 2));
        break;
      case 6:
        dest[destIndex++] = (byte) ((src.get(srcIndex) & 0x1f) |
            ((src.get(srcIndex + 1) & 0x07) << 5));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 1) & 0x18) >> 3) |
            ((src.get(srcIndex + 2) & 0x1f) << 2) |
            ((src.get(srcIndex + 3) & 0x01) << 7));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 3) & 0x1e) >> 1) |
            ((src.get(srcIndex + 4) & 0x0f) << 4));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 4) & 0x10) >> 4) |
            ((src.get(srcIndex + 5) & 0x1f) << 1));
        break;
      case 5:
        dest[destIndex++] = (byte) ((src.get(srcIndex) & 0x1f) |
            ((src.get(srcIndex + 1) & 0x07) << 5));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 1) & 0x18) >> 3) |
            ((src.get(srcIndex + 2) & 0x1f) << 2) |
            ((src.get(srcIndex + 3) & 0x01) << 7));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 3) & 0x1e) >> 1) |
            ((src.get(srcIndex + 4) & 0x0f) << 4));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 4) & 0x10) >> 4));
        break;
      case 4:
        dest[destIndex++] = (byte) ((src.get(srcIndex) & 0x1f) |
            ((src.get(srcIndex + 1) & 0x07) << 5));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 1) & 0x18) >> 3) |
            ((src.get(srcIndex + 2) & 0x1f) << 2) |
            ((src.get(srcIndex + 3) & 0x01) << 7));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 3) & 0x1e) >> 1));
        break;
      case 3:
        dest[destIndex++] = (byte) ((src.get(srcIndex) & 0x1f) |
            ((src.get(srcIndex + 1) & 0x07) << 5));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 1) & 0x18) >> 3) |
            ((src.get(srcIndex + 2) & 0x1f) << 2));
        break;
      case 2:
        dest[destIndex++] = (byte) ((src.get(srcIndex) & 0x1f) |
            ((src.get(srcIndex + 1) & 0x07) << 5));
        dest[destIndex++] = (byte) (((src.get(srcIndex + 1) & 0x18) >> 3));
        break;
      case 1:
        dest[destIndex++] = (byte) (src.get(srcIndex) & 0x1f);
        break;
    }
    return destIndex;
  }

}
