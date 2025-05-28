package org.orpheus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

class RecordAudioTest {
  @Test
  void test_audio_file() {
    RecordAudio recorder = new RecordAudio();
    recorder.set_file_path(System.getenv("HOME") + "/Downloads/recording.wav");

    assertTrue(recorder.get_file_path().equals(System.getenv("HOME") + "/Downloads/recording.wav"),
        "Set path should be changed.");

    File recorded_file = new File(recorder.get_file_path());
    recorder.record(10);

    assertTrue(recorded_file.exists(), "Audio file of the set path should be created.");
    assertTrue(recorded_file.length() > 0, "Audio file should be non-empty.");

    if (recorded_file.exists()) {
      recorded_file.delete();
    }
  }
}
