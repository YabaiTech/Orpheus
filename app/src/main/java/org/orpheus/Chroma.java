package org.orpheus;

import java.util.Arrays;

public class Chroma {
  private final double[] coefficients = { 0.25, 0.75, 1.0, 0.75, 0.25 };
  public static final int bandsLen = 12;
  private final int minFreq = 28;
  private final int maxFreq = 3520;
  private final int minIndex = Math.max(1, freqToIndex(minFreq));
  private final int maxIndex = Math.min(Constants.windowSize / 2, freqToIndex(maxFreq));

  public final int BUFFER_SIZE = 8;
  private int bufferLen;
  private int bufferI;
  private double[][] buffer;
  private double[] resultsBuffer;

  // Zig comp-time pre-calculated [section start]
  private int[] notes = null;
  // Zig comp-time pre-calculated [section end]

  Chroma() {
    this.bufferLen = 1;
    this.bufferI = 0;

    // We've initialized both of the buffers in the constrcutor here.
    // But in the original code, they didn't. They probable manually assigned the
    // buffer.
    this.buffer = new double[8][bandsLen];
    this.resultsBuffer = new double[bandsLen];

    // In the Zig implementation, they used compile-time evaluation to generate
    // `notes` array in compile-time for future use.
    //
    // But, Java doesn't support that. So, we do that in runtime using the following
    // function invocation. Rather than returning the array, we store the notes in
    // the `this.notes` data member.
    generateNotes();
  }

  private void generateNotes() {
    this.notes = new int[Constants.windowSize];

    for (int i = minIndex; i < maxIndex; i++) {
      double freq = indexToFreq(i);
      double octave = freqToOctave(freq);
      double note = bandsLen * (octave - Math.floor(octave));

      notes[i] = (int) Math.floor(note);
    }
  }

  public double[] filter(double[] fftFrame) {
    double[] buf = buffer[bufferI];
    Arrays.fill(buf, 0);

    for (int i = minIndex; i < maxIndex; i++) {
      int note = notes[i];
      double energy = fftFrame[i];

      buf[note] += energy;
    }

    bufferI = (bufferI + 1) % BUFFER_SIZE;

    if (bufferLen >= coefficients.length) {
      int offset = (bufferI + BUFFER_SIZE - coefficients.length) % BUFFER_SIZE;
      Arrays.fill(resultsBuffer, 0);

      for (int i = 0; i < bandsLen; i++) {
        for (int j = 0; j < coefficients.length; j++) {
          resultsBuffer[i] += buffer[(offset + j) % BUFFER_SIZE][i] * coefficients[j];
        }
      }

      normalize(resultsBuffer);
      return resultsBuffer;
    } else {
      bufferLen += 1;
      return null;
    }
  }

  private double euclideanNorm(double[] features) {
    double squares = 0;
    for (double feature : features) {
      squares += feature * feature;
    }

    if (Double.isInfinite(squares)) {
      throw new ArithmeticException("Eucledian norm overflowed!");
    }
    return (double) Math.sqrt(squares);
  }

  private void normalize(double[] features) {
    double norm = euclideanNorm(features);

    if (norm < 0.01) {
      Arrays.fill(features, 0.0);
    } else {
      for (int i = 0; i < features.length; i++) {
        features[i] /= norm;
      }
    }
  }

  private int freqToIndex(double freq) {
    return (int) Math.round(Constants.windowSize * freq / (double) Constants.sampleRate);
  }

  private double indexToFreq(double i) {
    return i * Constants.sampleRate / (double) Constants.windowSize;
  }

  private double freqToOctave(double freq) {
    final double base = 440.0 / 16.0;
    final double x = freq / base;

    // Java doesn't have `log base 2` functionality in the stdlib
    // So, we use: log2(x) = ln(x) / ln(2)
    return Math.log(x) / Math.log(2);
  }
}
