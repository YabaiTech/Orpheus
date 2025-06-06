package org.orpheus;

import java.io.*;
import javax.sound.sampled.*;

public class ProcessAudio {
  private static final AudioFormat TARGET_FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
      Constants.sampleRate, Constants.bitsPerSample, Constants.channelCount, Constants.bytesPerFrame,
      Constants.framesPerSecond, false);

  public static double[] parse(String file) {
    try {
      AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(file));

      AudioInputStream pcmStream = AudioSystem.getAudioInputStream(TARGET_FORMAT, inputStream);
      inputStream.close();

      byte[] streamBytes = pcmStream.readAllBytes();
      pcmStream.close();

      int sampleSize = TARGET_FORMAT.getSampleSizeInBits() / 8;
      int channelCount = TARGET_FORMAT.getChannels();
      int totalFrames = streamBytes.length / (sampleSize * channelCount);

      double[] samples = new double[totalFrames];
      for (int frame = 0, sampleIdx = 0; frame < totalFrames; ++frame) {
        int low = streamBytes[sampleIdx] & 0xFF;
        int high = streamBytes[sampleIdx + 1] << 8;
        int signed16 = high | low;

        samples[frame] = signed16 / 32768.0;
        sampleIdx += sampleSize * channelCount;
      }

      return samples;
    } catch (UnsupportedAudioFileException | IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
