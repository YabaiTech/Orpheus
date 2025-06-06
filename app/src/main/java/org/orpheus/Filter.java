package org.orpheus;

class Filter {
  public int type;
  public int y;
  public int height;
  public int width;

  Filter(int t, int y, int height, int width) {
    if (t > 5) {
      throw new IllegalStateException("[Filter] Type(t) is greater than five!");
    }

    this.type = t;
    this.y = y;
    this.height = height;
    this.width = width;
  }
}
