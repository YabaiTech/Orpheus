package org.orpheus;

import java.io.*;
import javax.sound.sampled.*;

public class RecordAudio {
  private String filePath = System.getenv("HOME") + "/recording.wav";
  private File outputFile = new File(this.filePath);
  private TargetDataLine line;
  private Thread recordingThread;

  public String getFilePath() {
    return this.filePath;
  }

  public void setFilePath(String path) {
    if (!path.endsWith(".wav")) {
      System.out.println("ERROR: Invalid file name.");
      System.exit(1);
    }

    this.filePath = path;
    this.outputFile = new File(this.filePath);
  }

  private AudioFormat getAudioFormat() {
    float rate = 11025f;
    int sizeInBits = 16;
    int channels = 1;
    boolean signed = true;
    boolean bigEndian = false;

    return new AudioFormat(rate, sizeInBits, channels, signed, bigEndian);
  }

  private void start() {
    try {
      AudioFormat audioFormat = this.getAudioFormat();
      DataLine.Info datalineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

      if (!AudioSystem.isLineSupported(datalineInfo)) {
        System.out.println("Dataline is unsupported.");
        System.exit(1);
      }

      this.line = (TargetDataLine) AudioSystem.getLine(datalineInfo);
      this.line.open(audioFormat);
      this.line.start();
      System.out.println("Capturing audio now...");

      this.recordingThread = new Thread(() -> {
        try {
          AudioInputStream inputStream = new AudioInputStream(this.line);
          AudioSystem.write(inputStream, AudioFileFormat.Type.WAVE, this.outputFile);

        } catch (IOException e) {
          e.printStackTrace();
        }
      });

      this.recordingThread.start();
      System.out.println("Writing captured audio to disk now...");
    } catch (LineUnavailableException e) {
      e.printStackTrace();
    }
  }

  private void finish() {
    this.line.stop();
    this.line.close();
    System.out.println("Saved recorded file as: " + filePath);
  }

  public void record(int duration) {
    this.start();

    try {
      Thread.sleep(duration * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    this.finish();
  }
}
