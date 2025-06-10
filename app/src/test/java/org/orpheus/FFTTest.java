package org.orpheus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class FFTTest {
  @Test
  void testSine() {
    final int nFrames = 3;
    final int frameSize = 32;
    final int overlap = 8;
    final int sampleRate = 1000;
    final double freq = 7 * (sampleRate / 2) / (frameSize / 2);
    int[] input = new int[frameSize + (nFrames - 1) * (frameSize - overlap)];
    for (int i = 0; i < input.length; ++i) {
      input[i] = (int) (Short.MAX_VALUE * Math.sin(i * freq * 2.0 * Math.PI / sampleRate));
    }

    int[] expectedInput = {
        0, 32106, 12824, -26984, -23602, 17557, 30615, -5329, -32743, -7749, 29648, 19591, -21823, -28308, 10516, 32508,
        2468, -31522, -15059, 25507, 25247, -15423, -31407, 2878, 32557, 10125, -28513, -21514, 19920, 29470, -8148,
        -32725, -4922, 30759, 17208, -23886, -26748, 13202, 32022, -411, -32186, -12444, 27216, 23314, -17903, -30465,
        5734, 32756, 7348, -29821, -19259, 22128, 28098, -10905, -32454, -2057, 31632, 14692, -25764, -24982, 15785,
        31288, -3288, -32601, -9733, 28713, 21202, -20245, -29288, 8546, 32702, 4514, -30898, -16856, 24166, 26509,
        -13577, -31932, 823, 32261, };
    for (int i = 0; i < input.length; ++i) {
      assertEquals(input[i], expectedInput[i]);
    }
  }
}
