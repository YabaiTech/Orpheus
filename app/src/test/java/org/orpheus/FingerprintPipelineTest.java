package org.orpheus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

class FingerprintPipelineTest {
  @Test
  public void testEmptySamples() {
    byte[] fingerprint = new FingerprintPipeline().calculateFingerPrint(new double[0]);
    assertNull(fingerprint, "Fingerprint should be null for an empty sample.");
  }

  @Test
  public void testShortSamples() {
    int shortLen = Constants.windowSize - 1;
    double samples[] = new double[shortLen];
    Arrays.fill(samples, 0.0);

    byte[] fingerprint = new FingerprintPipeline().calculateFingerPrint(samples);
    assertNull(fingerprint, "Fingerprint should be null for samples shorter than 4096.");
  }

  @Test
  // NOTE: this test fails, there is something wrong with the chromaprint
  // implementation
  public void testConsistentFingerprint() {
    FingerprintPipeline fingerprintPipeline = new FingerprintPipeline();

    double[] samples = new double[Constants.windowSize + Constants.hopSize * 19];
    byte[] fp1 = fingerprintPipeline.calculateFingerPrint(samples);
    byte[] fp2 = fingerprintPipeline.calculateFingerPrint(samples);

    assertNotNull(fp1, "Fingerprint should be non-null.");
    assertNotNull(fp2, "Fingerprint should be non-null.");
    assertArrayEquals(fp1, fp2, "Fingerprint calculation on the same sample should always be identical");
  }
}
