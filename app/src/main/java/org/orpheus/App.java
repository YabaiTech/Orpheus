package org.orpheus;

public class App {
  public static void main(String[] args) {
    RecordAudio recorder = new RecordAudio();
    recorder.record(10);
  }
}
