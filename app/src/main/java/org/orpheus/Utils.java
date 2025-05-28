package org.orpheus;

public class Utils {
  public static OS detect_platform() {
    String os = System.getProperty("os.name").toLowerCase();

    if (os.contains("linux")) {
      return OS.LINUX;
    } else if (os.contains("win")) {
      return OS.WINDOWS;
    } else {
      return OS.MACOS;
    }
  }
}
