package com.dotcms.embedded;

public class SneakyThrow {
  @SuppressWarnings("unchecked")
  public static <T extends Throwable> void sneak(Throwable t) throws T {
    throw (T) t;
  }
}
