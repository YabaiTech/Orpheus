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

    assertTrue(c.bandsLen == 12, "Check if Chroma's band length is properly set");
  }
}
