package org.orpheus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

class RecordAudioTest {
  @Test
  void testAudioFile() {
    RecordAudio recorder = new RecordAudio();
    recorder.setFilePath(System.getenv("HOME") + "/Downloads/recording.wav");

    assertTrue(recorder.getFilePath().equals(System.getenv("HOME") + "/Downloads/recording.wav"),
        "Set path should be changed.");

    File recordedFile = new File(recorder.getFilePath());
    recorder.record(10);

    assertTrue(recordedFile.exists(), "Audio file of the set path should be created.");
    assertTrue(recordedFile.length() > 0, "Audio file should be non-empty.");

    if (recordedFile.exists()) {
      recordedFile.delete();
    }
  }
}
