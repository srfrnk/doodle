package common;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logging {
  public static String exception(Exception ex) {
    StringWriter sw = new StringWriter();
    ex.printStackTrace(new PrintWriter(sw));
    String stacktrace = sw.toString();
    return stacktrace;
  }
}