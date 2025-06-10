package org.orpheus;

import java.util.Arrays;

public class RollingIntegralImage {
  public static final int MAX_ROWS = 256 + 1;
  public static final int NUM_COLUMNS = Chroma.bandsLen;
  public static final int DATA_SIZE = MAX_ROWS * NUM_COLUMNS;

  private double[] data;
  public int numRows;

  RollingIntegralImage() {
    this.data = new double[DATA_SIZE];
    this.numRows = 0;
  }

  public void addRow(double[] features) {
    RowView currentRow = getRow(numRows);

    double accum = 0.0;
    for (int i = 0; i < NUM_COLUMNS; i++) {
      if (Double.isNaN(features[i])) {
        throw new IllegalArgumentException("[RollingIntegralImage] features[i] is NaN");
      }

      accum += features[i];
      currentRow.set(i, accum);
    }

    if (numRows > 0) {
      RowView lastRow = getRow(numRows - 1);
      for (int i = 0; i < NUM_COLUMNS; i++) {
        currentRow.set(i, currentRow.get(i) + lastRow.get(i));
      }
    }
    numRows++;
  }

  public RowView getRow(int i) {
    int startIdx = (i % MAX_ROWS) * NUM_COLUMNS;
    return new RowView(data, startIdx, NUM_COLUMNS);
  }

  public double[] getRowConst(int i) {
    return Arrays.copyOfRange(data, (i % MAX_ROWS) * NUM_COLUMNS, (i % MAX_ROWS + 1) * NUM_COLUMNS);
  }

  public double area(int r1, int c1, int r2, int c2) {
    if (r1 > numRows) {
      throw new IllegalArgumentException("[RollingIntegralImage] Passed argument r1 > numRows");
    }
    if (r2 > numRows) {
      throw new IllegalArgumentException("[RollingIntegralImage] Passed argument r2 > numRows");
    }

    if (numRows > MAX_ROWS) {
      if (r1 <= numRows - MAX_ROWS) {
        throw new IllegalArgumentException("[RollingIntegralImage] Passed argument r1 >= (numRows - MAX_ROWS)");
      }
      if (r2 <= numRows - MAX_ROWS) {
        throw new IllegalArgumentException("[RollingIntegralImage] Passed argument r2 >= (numRows - MAX_ROWS)");
      }
    }

    if (c1 > NUM_COLUMNS) {
      throw new IllegalArgumentException("[RollingIntegralImage] Passed argument c1 > NUM_COLUMNS");
    }
    if (c2 > NUM_COLUMNS) {
      throw new IllegalArgumentException("[RollingIntegralImage] Passed argument c2 > NUM_COLUMNS");
    }

    if (r1 == r2 || c1 == c2) {
      return 0;
    }

    if (r2 <= r1) {
      throw new IllegalArgumentException("[RollingIntegralImage] The passed argument r2 <= r1");
    }
    if (c2 <= c1) {
      throw new IllegalArgumentException("[RollingIntegralImage] The passed argument c2 <= c1");
    }

    if (r1 == 0) {
      double[] row = getRowConst(r2 - 1);
      if (c1 == 0) {
        return row[c2 - 1];
      } else {
        return row[c2 - 1] - row[c1 - 1];
      }
    } else {
      double[] row1 = getRowConst(r1 - 1);
      double[] row2 = getRowConst(r2 - 1);
      if (c1 == 0) {
        return row2[c2 - 1] - row1[c2 - 1];
      } else {
        return row2[c2 - 1] - row1[c2 - 1] - row2[c1 - 1] + row1[c1 - 1];
      }
    }
  }

  public int classify(Classifier classifier, int offset) {
    double value = applyFilter(classifier.filter, offset);
    return classifier.quantizer.quantize(value);
  }

  public double applyFilter(Filter filter, int x) {
    switch (filter.type) {
      case 0:
        return filter0(x, filter.y, filter.width, filter.height);
      case 1:
        return filter1(x, filter.y, filter.width, filter.height);
      case 2:
        return filter2(x, filter.y, filter.width, filter.height);
      case 3:
        return filter3(x, filter.y, filter.width, filter.height);
      case 4:
        return filter4(x, filter.y, filter.width, filter.height);
      case 5:
        return filter5(x, filter.y, filter.width, filter.height);
      default:
        throw new IllegalArgumentException("[RollingIntegralImage] Unknown filter type");
    }
  }

  private double filter0(int x, int y, int w, int h) {
    if (w < 1 | h < 1) {
      throw new IllegalArgumentException(
          "[RollingIntegralImage] Filter0: Either w < 1 or h < 1, which are invalid values");
    }

    double a = area(x, y, x + w, y + h);
    double b = 0;
    return subtractLog(a, b);
  }

  private double filter1(int x, int y, int w, int h) {
    if (w < 1 | h < 1) {
      throw new IllegalArgumentException(
          "[RollingIntegralImage] Filter1: Either w < 1 or h < 1, which are invalid values");
    }

    int h2 = h / 2;
    double a = area(x, y + h2, x + w, y + h);
    double b = area(x, y, x + w, y + h2);
    return subtractLog(a, b);
  }

  private double filter2(int x, int y, int w, int h) {
    if (w < 1 | h < 1) {
      throw new IllegalArgumentException(
          "[RollingIntegralImage] Filter2: Either w < 1 or h < 1, which are invalid values");
    }

    int w2 = w / 2;
    double a = area(x + w2, y, x + w, y + h);
    double b = area(x, y, x + w2, y + h);
    return subtractLog(a, b);
  }

  private double filter3(int x, int y, int w, int h) {
    if (x < 0 | y < 0) {
      throw new IllegalArgumentException(
          "[RollingIntegralImage] Filter3: Either x < 0 or y < 0, which are invalid values");
    }
    if (w < 1 | h < 1) {
      throw new IllegalArgumentException(
          "[RollingIntegralImage] Filter3: Either w < 1 or h < 1, which are invalid values");
    }

    int w2 = w / 2;
    int h2 = h / 2;
    double a = area(x, y + h2, x + w2, y + h) + area(x + w2, y, x + w, y + h2);
    double b = area(x, y, x + w2, y + h2) + area(x + w2, y + h2, x + w, y + h);
    return subtractLog(a, b);
  }

  private double filter4(int x, int y, int w, int h) {
    if (w < 1 | h < 1) {
      throw new IllegalArgumentException(
          "[RollingIntegralImage] Filter4: Either w < 1 or h < 1, which are invalid values");
    }

    int h3 = h / 3;
    double a = area(x, y + h3, x + w, y + 2 * h3);
    double b = area(x, y, x + w, y + h3) + area(x, y + 2 * h3, x + w, y + h);
    return subtractLog(a, b);
  }

  private double filter5(int x, int y, int w, int h) {
    if (w < 1 | h < 1) {
      throw new IllegalArgumentException(
          "[RollingIntegralImage] Filter5: Either w < 1 or h < 1, which are invalid values");
    }

    int w3 = w / 3;
    double a = area(x + w3, y, x + 2 * w3, y + h);
    double b = area(x, y, x + w3, y + h) + area(x + 2 * w3, y, x + w, y + h);
    return subtractLog(a, b);
  }

  private double subtractLog(double a, double b) {
    double r = (double) Math.log((1.0 + a) / (1.0 + b));
    assert !Double.isNaN(r);
    if (Double.isNaN(r)) {
      throw new ArithmeticException("[RollingIntegralImage] Computed r is NaN");
    }
    return r;
  }
}
