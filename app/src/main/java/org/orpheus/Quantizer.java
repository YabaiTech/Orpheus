package org.orpheus;

class Quantizer {
  private double[] t = new double[3];

  Quantizer(double[] thresholds) {
    if (thresholds.length != 3) {
      throw new IllegalArgumentException("[Quantizer] Quantizer requires exactly 3 threshold values!");
    }

    this.t = thresholds.clone();
  }

  public int quantize(double value) {
    if (value < t[1]) {
      return (value < t[0]) ? 0 : 1;
    } else {
      return (value < t[2]) ? 2 : 3;
    }
  }
}
