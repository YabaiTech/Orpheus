package org.orpheus;

class Constants {
  public static final int sampleRate = 11025;
  public static final String sampleRateString = "11025";
  public static final int windowSize = 4096;
  public static final int hopSize = windowSize / 2;
  public static final int channelCount = 1;
  public static final int bitsPerSample = 16;
  public static final int bytesPerFrame = 2;
  public static final int framesPerSecond = sampleRate;
}
