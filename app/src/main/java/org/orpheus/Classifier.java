package org.orpheus;

class Classifier {
  public Filter filter;
  public Quantizer quantizer;

  public Classifier(Filter f, Quantizer q) {
    if (f == null || q == null) {
      throw new IllegalArgumentException("[Classifier] One or both of the passed filters were null");
    }

    this.filter = f;
    this.quantizer = q;
  }
}
