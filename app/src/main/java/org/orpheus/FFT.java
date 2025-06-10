package org.orpheus;

public class FFT {
  public static Complex[] fft(Complex[] z) {
    int n = z.length;
    if (n == 1) {
      return new Complex[] { z[0] };
    }
    // radix 2 Cooley-Tukey FFT
    if ((n & (n - 1)) != 0) {
      throw new IllegalArgumentException("ERROR: Length must be an power of 2.");
    }

    // compute for even terms
    Complex[] even = new Complex[n / 2];
    for (int i = 0; i < n / 2; ++i) {
      even[i] = z[2 * i];
    }
    Complex[] e = fft(even);

    // compute for odd terms
    Complex[] odd = new Complex[n / 2];
    for (int i = 0; i < n / 2; ++i) {
      odd[i] = z[2 * i + 1];
    }
    Complex[] o = fft(odd);

    // combine both
    Complex[] y = new Complex[n];
    for (int i = 0; i < n / 2; ++i) {
      double k = -2 * i * Math.PI / n;
      Complex ck = new Complex(Math.cos(k), Math.sin(k));

      y[i] = e[i].sum(ck.multiply(o[i]));
      y[i + n / 2] = e[i].subtract(ck.multiply(o[i]));
    }

    return y;
  }
}
