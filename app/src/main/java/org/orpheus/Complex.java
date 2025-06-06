package org.orpheus;

public class Complex {
  private double real;
  private double imaginary;

  public Complex(double re, double im) {
    this.real = re;
    this.imaginary = im;
  }

  public double getReal() {
    return this.real;
  }

  public double getImaginary() {
    return this.imaginary;
  }

  public Complex sum(Complex z) {
    return new Complex(this.real + z.real, this.imaginary + z.imaginary);
  }

  public Complex subtract(Complex z) {
    return new Complex(this.real - z.real, this.imaginary - z.imaginary);
  }

  public Complex multiply(Complex z) {
    double re = this.real * z.real - this.imaginary * z.imaginary;
    double im = this.real * z.imaginary + this.imaginary * z.real;

    return new Complex(re, im);
  }

  public Complex scale(double c) {
    return new Complex(c * this.real, c * this.imaginary);
  }
}
