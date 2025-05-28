package org.orpheus;

import java.io.*;

import javax.sound.sampled.*;

public class RecordAudio {
  private String file_path = System.getenv("HOME") + "/recording.wav";
  private File output_file = new File(this.file_path);
  private TargetDataLine line;
  private Thread recording_thread;

  public void set_file(String path) {
    if (!path.endsWith(".wav")) {
      System.out.println("ERROR: Invalid file name.");
      System.exit(1);
    }

    this.file_path = path;
    this.output_file = new File(this.file_path);
  }

  private AudioFormat get_audio_format() {
    float rate = 16000.0F;
    int size_in_bits = 8;
    int channels = 2;
    boolean signed = true;
    boolean big_endian = true;

    return new AudioFormat(rate, size_in_bits, channels, signed, big_endian);
  }

  private void start() {
    try {
      AudioFormat audio_format = this.get_audio_format();
      DataLine.Info dataline_info = new DataLine.Info(TargetDataLine.class, audio_format);

      if (!AudioSystem.isLineSupported(dataline_info)) {
        System.out.println("Dataline is unsupported.");
        System.exit(1);
      }

      this.line = (TargetDataLine) AudioSystem.getLine(dataline_info);
      this.line.open(audio_format);
      this.line.start();
      System.out.println("Capturing audio now...");

      this.recording_thread = new Thread(() -> {
        try {
          AudioInputStream input_stream = new AudioInputStream(this.line);
          AudioSystem.write(input_stream, AudioFileFormat.Type.WAVE, this.output_file);

        } catch (IOException e) {
          e.printStackTrace();
        }
      });

      this.recording_thread.start();
      System.out.println("Writing captured audio to disk now...");
    } catch (LineUnavailableException e) {
      e.printStackTrace();
    }
  }

  private void finish() {
    this.line.stop();
    this.line.close();
    System.out.println("Saved recorded file as: " + file_path);
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
