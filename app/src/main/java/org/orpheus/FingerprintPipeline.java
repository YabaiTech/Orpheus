package org.orpheus;

import java.util.List;

import java.util.ArrayList;

public class FingerprintPipeline {
  private final int WINDOW_SIZE = Constants.windowSize;
  private final int HOP_SIZE = Constants.hopSize;

  // step 1 (already done via class ProcessAudio): fetch samples array
  public int[] calculateFingerPrint(double[] samples) {
    try {
      // step 2: split the samples into 4096 windows/frames with 50% overlap
      List<double[]> frames = new ArrayList<>();
      for (int i = 0; i + this.WINDOW_SIZE <= samples.length; i += HOP_SIZE) {
        double[] frame = new double[this.WINDOW_SIZE];

        System.arraycopy(samples, i, frame, 0, this.WINDOW_SIZE);
        frames.add(frame);
      }

      // add padding for the potential remaining samples
      int leftover = samples.length % this.HOP_SIZE;
      if (leftover != 0 && samples.length > frames.size() * HOP_SIZE) {
        double[] leftoverFrame = new double[this.WINDOW_SIZE];
        int start = frames.size() * HOP_SIZE;
        int leftoverLength = samples.length - start;

        System.arraycopy(samples, start, leftoverFrame, 0, leftoverLength);
        frames.add(leftoverFrame);
      }

      // step 3: precompute Hamming(4096)
      double[] hamming = new Hamming().hamming;
      Chroma chroma = new Chroma();
      FingerprintCalculator fingerprintCalculator = new FingerprintCalculator();

      for (double[] frame : frames) {
        // step 4: apply Hamming to pack into Complex[4096]
        Complex[] complexWindow = new Complex[this.WINDOW_SIZE];
        for (int i = 0; i < this.WINDOW_SIZE; ++i) {
          double windowed = frame[i] * hamming[i];
          complexWindow[i] = new Complex(windowed, 0.0);
        }

        // step 5: apply on the complex window
        Complex[] spectrum = FFT.fft(complexWindow);

        // step 6: compute magnitudes from the spectrum
        double[] magnitudes = new double[this.WINDOW_SIZE / 2 + 1];
        for (int i = 0; i < magnitudes.length; ++i) {
          double re = spectrum[i].getReal();
          double im = spectrum[i].getImaginary();

          magnitudes[i] = Math.hypot(re, im);
        }

        // step 7: pass magnitudes[] into Chroma::filter(...) which should expect
        // a double[2049] (fftFrame) and returns either null or double[12]
        double[] chromaFiltered = chroma.filter(magnitudes);
        if (chromaFiltered != null) {
          // step 8: if non-null was returned, add it to the fingerprint
          fingerprintCalculator.addFeatures(chromaFiltered);
        }
      }

      // step 9: extract integer fingerprints and convert to int[] for
      // compression (optional?)
      List<Integer> fingerprint = fingerprintCalculator.fingerprint;
      int[] fingerprintRes = new int[fingerprint.size()];
      for (int i = 0; i < fingerprint.size(); ++i) {
        fingerprintRes[i] = fingerprint.get(i);
      }
      fingerprintCalculator.close();

      return fingerprintRes;
    } catch (Exception e) {
      System.out.println("[FingerprintPipeline] ERROR: ");
      e.printStackTrace();
      return null;
    }
  }

  public byte[] compressFingerprint(int[] fingerprint) {
    if (fingerprint == null || fingerprint.length == 0) {
      return null;
    }

    // step 10: compress into byte[]
    Compress compressor = new Compress(fingerprint.length, fingerprint.length / 10);

    return compressor.compress(fingerprint);
  }
}
