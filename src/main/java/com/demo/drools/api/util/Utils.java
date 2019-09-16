package com.demo.drools.api.util;

public class Utils {
  public static String getKieSessionName(String artifactId) {
    return "ksession-" + artifactId;
  }
}
