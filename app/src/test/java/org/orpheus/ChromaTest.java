// To run all the tests: gradle test
//
// To only run this test, use the `--test` flag and pass the name of this test class:
// gradle test --test ChromaTest

package org.orpheus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChromaTest {
  @Test
  void testSampling() {
    Chroma c = new Chroma();

    assertTrue(c.bufferLen == 1, "Check if Chroma's buffer length is properly set");
    assertTrue(c.bufferI == 0, "Check if Chroma's buffer index is properly set");
  }
}
