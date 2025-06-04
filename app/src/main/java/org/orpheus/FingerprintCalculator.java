package org.orpheus;

import java.util.ArrayList;
import org.orpheus.Filter;

public class FingerprintCalculator implements AutoCloseable {
  public RollingIntegralImage image;
  public ArrayList<Integer> fingerprint;
  // we don't need an allocator since Java provides us with one automatically

  public int maxFilterWidth;

  FingerprintCalculator() {
    this.image = new RollingIntegralImage();
    this.fingerprint = new ArrayList<Integer>();

    // this was done it comp-time in Zig
    calculateMaxFilterWidth();
  }

  // we use the free the resources explicitly (more of a symbolic thing as the GC
  // would have free'd it anyway)
  @Override
  public void close() {
    this.image = null;
    this.fingerprint = null;
  }

  // filters
  Filter f1 = new Filter(0, 4, 3, 15);
  Filter f2 = new Filter(4, 4, 6, 15);
  Filter f3 = new Filter(1, 0, 4, 16);
  Filter f4 = new Filter(3, 8, 2, 12);

  Filter f5 = new Filter(3, 4, 4, 8);
  Filter f6 = new Filter(4, 0, 3, 5);
  Filter f7 = new Filter(1, 2, 2, 9);
  Filter f8 = new Filter(2, 7, 3, 4);

  Filter f9 = new Filter(2, 6, 2, 16);
  Filter f10 = new Filter(2, 1, 3, 2);
  Filter f11 = new Filter(5, 10, 1, 15);
  Filter f12 = new Filter(3, 6, 2, 10);

  Filter f13 = new Filter(2, 1, 1, 14);
  Filter f14 = new Filter(3, 5, 6, 4);
  Filter f15 = new Filter(1, 9, 2, 12);
  Filter f16 = new Filter(3, 4, 2, 14);

  // quantizers
  Quantizer q1 = new Quantizer(new double[] { 1.98215, 2.35817, 2.63523 });
  Quantizer q2 = new Quantizer(new double[] { -1.03809, -0.651211, -0.282167 });
  Quantizer q3 = new Quantizer(new double[] { -0.298702, 0.119262, 0.558497 });
  Quantizer q4 = new Quantizer(new double[] { -0.105439, 0.0153946, 0.135898 });

  Quantizer q5 = new Quantizer(new double[] { -0.142891, 0.0258736, 0.200632 });
  Quantizer q6 = new Quantizer(new double[] { -0.826319, -0.590612, -0.368214 });
  Quantizer q7 = new Quantizer(new double[] { -0.557409, -0.233035, 0.0534525 });
  Quantizer q8 = new Quantizer(new double[] { -0.0646826, 0.00620476, 0.0784847 });

  Quantizer q9 = new Quantizer(new double[] { -0.192387, -0.029699, 0.215855 });
  Quantizer q10 = new Quantizer(new double[] { -0.0397818, -0.00568076, 0.0292026 });
  Quantizer q11 = new Quantizer(new double[] { -0.53823, -0.369934, -0.190235 });
  Quantizer q12 = new Quantizer(new double[] { -0.124877, 0.0296483, 0.139239 });

  Quantizer q13 = new Quantizer(new double[] { -0.101475, 0.0225617, 0.231971 });
  Quantizer q14 = new Quantizer(new double[] { -0.0799915, -0.00729616, 0.063262 });
  Quantizer q15 = new Quantizer(new double[] { -0.272556, 0.019424, 0.302559 });
  Quantizer q16 = new Quantizer(new double[] { -0.164292, -0.0321188, 0.0846339 });

  final Classifier[] classifiers = {
      new Classifier(f1, q1), new Classifier(f2, q2), new Classifier(f3, q3),
      new Classifier(f4, q4), new Classifier(f5, q5), new Classifier(f6, q6),
      new Classifier(f7, q7), new Classifier(f8, q8), new Classifier(f9, q9),
      new Classifier(f10, q10), new Classifier(f11, q11), new Classifier(f12, q12),
      new Classifier(f13, q13), new Classifier(f14, q14), new Classifier(f15, q15),
      new Classifier(f16, q16) };

  // this was done it comp-time in Zig
  void calculateMaxFilterWidth() {
    int result = 0;

    for (Classifier c : classifiers) {
      result = Math.max(result, c.filter.width);
    }

    if (result <= 0 || result >= 256) {
      throw new IllegalArgumentException("[FingerprintCalculator] Calculated MaxFilterWidth is invalid!");
    }

    maxFilterWidth = result;
  }

  public void addFeatures(double[] features) {
    image.addRow(features);
    if (image.numRows >= maxFilterWidth) {
      final int subFingerprint = calculateSubFingerprint(image.numRows - maxFilterWidth);

      // If the audio is long enough, the following line could throw due to lack of
      // memory
      fingerprint.add(subFingerprint);
    }
  }

  public int calculateSubFingerprint(int offset) {
    final int[] grayCodes = new int[] { 0, 1, 3, 2 };
    int bits = 0;

    for (Classifier c : classifiers) {
      bits = (bits << 2) | grayCodes[image.classify(c, offset)];
    }

    return bits;
  }

}
