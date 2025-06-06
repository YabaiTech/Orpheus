package org.orpheus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

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

  @Test
  public void testShortSamples() {
    int shortLen = Constants.windowSize - 1;
    double samples[] = new double[shortLen];
    Arrays.fill(samples, 0.0);

    FingerprintPipeline fingerprintPipeline = new FingerprintPipeline();

    int[] fingerprint = fingerprintPipeline.calculateFingerPrint(samples);
    byte[] compressedFingerprint = fingerprintPipeline.compressFingerprint(fingerprint);
    assertNull(fingerprint, "Raw fingerprint should be null for samples shorter than 4096.");
    assertNull(compressedFingerprint, "Fingerprint should be null for samples shorter than 4096.");
  }

  @Test
  // NOTE: this test fails, there is something wrong with the chromaprint
  // implementation
  public void testConsistentFingerprint() {
    FingerprintPipeline fingerprintPipeline = new FingerprintPipeline();

    double[] samples = new double[Constants.windowSize + Constants.hopSize * 19];
    int[] fp1 = fingerprintPipeline.calculateFingerPrint(samples);
    byte[] cfp1 = fingerprintPipeline.compressFingerprint(fp1);
    int[] fp2 = fingerprintPipeline.calculateFingerPrint(samples);
    byte[] cfp2 = fingerprintPipeline.compressFingerprint(fp2);

    // still fails if only raw fingerprints are checked. conclusion: something(s)
    // has gone wrong in the java port
    assertNotNull(fp1, "Raw fingerprint should be non-null.");
    assertNotNull(fp2, "Raw fingerprint should be non-null.");
    assertArrayEquals(fp1, fp2, "Raw fingerprint calculation on the same sample should always be identical");
    // assertNotNull(cfp2, "Fingerprint should be non-null.");
    // assertNotNull(cfp2, "Fingerprint should be non-null.");
    // assertArrayEquals(cfp1, cfp2, "Fingerprint calculation on the same sample
    // should always be identical");
  }
}
