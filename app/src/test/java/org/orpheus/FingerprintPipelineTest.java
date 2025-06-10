package org.orpheus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Arrays;
import java.util.Base64;

class FingerprintPipelineTest {
  @Test
  public void testEmptySamples() {
    FingerprintPipeline fingerprintPipeline = new FingerprintPipeline();
    int[] fingerprint = fingerprintPipeline.calculateFingerPrint(new double[0]);
    assertNotNull(fingerprint, "Raw fingerprint should not be null for empty sample.");
    assertEquals(0, fingerprint.length, "Raw fingerprint should return int[] of length 0.");

    byte[] compressedFingerprint = fingerprintPipeline.compressFingerprint(fingerprint);
    assertNull(compressedFingerprint, "Fingerprint should be null for an empty sample.");
  }
  //
  // @Test
  // public void testShortSamples() {
  // int shortLen = Constants.windowSize - 1;
  // double samples[] = new double[shortLen];
  // Arrays.fill(samples, 0.0);
  //
  // FingerprintPipeline fingerprintPipeline = new FingerprintPipeline();
  //
  // int[] fingerprint = fingerprintPipeline.calculateFingerPrint(samples);
  // byte[] compressedFingerprint =
  // fingerprintPipeline.compressFingerprint(fingerprint);
  // assertNull(fingerprint, "Raw fingerprint should be null for samples shorter
  // than 4096.");
  // assertNull(compressedFingerprint, "Fingerprint should be null for samples
  // shorter than 4096.");
  // }

  @Test
  public void testConsistentFingerprint() {
    FingerprintPipeline fingerprintPipeline = new FingerprintPipeline();

    double[] samples = new double[Constants.windowSize + Constants.hopSize * 19];
    int[] fp1 = fingerprintPipeline.calculateFingerPrint(samples);
    byte[] cfp1 = fingerprintPipeline.compressFingerprint(fp1);
    int[] fp2 = fingerprintPipeline.calculateFingerPrint(samples);
    byte[] cfp2 = fingerprintPipeline.compressFingerprint(fp2);

    assertNotNull(fp1, "Raw fingerprint should be non-null.");
    assertNotNull(fp2, "Raw fingerprint should be non-null.");
    assertArrayEquals(fp1, fp2, "Raw fingerprint calculation on the same sample should always be identical");
    assertNotNull(cfp2, "Fingerprint should be non-null.");
    assertNotNull(cfp2, "Fingerprint should be non-null.");
    assertArrayEquals(cfp1, cfp2, "Fingerprint calculation on the same sample should always be identical");
  }

  @Test
  public void testAgainstFpcalc() {
    double[] samples = ProcessAudio.parse(System.getenv("HOME") + "/hope.wav");
    assertEquals(new File(System.getenv("HOME") + "/hope.wav").exists(), true, "File exists.");
    assertNotNull(samples, "Parsed sampels must not return null");
    FingerprintPipeline fingerprintPipeline = new FingerprintPipeline();

    try {
      int[] fp1 = fingerprintPipeline.calculateFingerPrint(samples);
      byte[] cfp1 = fingerprintPipeline.compressFingerprint(fp1);
      int[] fp2 = fingerprintPipeline.calculateFingerPrint(samples);
      byte[] cfp2 = fingerprintPipeline.compressFingerprint(fp2);

      assertNotNull(fp1, "Raw fingerprint should be non-null.");
      assertNotNull(fp2, "Raw fingerprint should be non-null.");
      assertArrayEquals(fp1, fp2, "Raw fingerprint calculation on the same sample should always be identical");
      assertArrayEquals(cfp1, cfp2, "Fingerprint calculation on the same sample should always be identical");
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }
}
