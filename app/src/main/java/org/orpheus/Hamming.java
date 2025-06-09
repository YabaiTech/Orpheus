package org.orpheus;

class Hamming {
  public double[] hamming = null;

  Hamming() {
    // In the Zig implementation, they used compile-time evaluation to get an
    // already filled array for the following things.
    //
    // But, Java doesn't support that. So, we do that in runtime using the following
    // function invocations. Rather than returning the array, we store the notes in
    // their appropriate data memberss.
    generateHamming(Constants.windowSize);
  }

  private void generateHamming(int windowSize) {
    double[] result = new double[windowSize];

    for (int i = 0; i < windowSize; i++) {
      result[i] = (double) (0.54 - 0.46 * Math.cos(2.0 * Math.PI * i / (windowSize - 1)));
    }

    this.hamming = result;
  }
}
