package org.orpheus;

public class RowView {
  private final double[] data;
  private final int offset;
  private final int length;

  public RowView(double[] data, int offset, int length) {
    this.data = data;
    this.offset = offset;
    this.length = length;
  }

  public double get(int index) {
    return data[offset + index];
  }

  public void set(int index, double value) {
    data[offset + index] = value;
  }

  public int length() {
    return length;
  }
}
