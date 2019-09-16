package com.demo.drools.api.model;

public enum GegevensType {

  BOOLEAN("boolean"),

  NUMBER("number"),

  LIST("list"),

  DATE("date"),

  STRING("string");
  private final String value;

  GegevensType(String v) {
    value = v;
  }

  public String value() {
    return value;
  }

  public static GegevensType fromValue(String v) {
    for (GegevensType c : GegevensType.values()) {
      if (c.value.equals(v)) {
        return c;
      }
    }
    throw new IllegalArgumentException(v);
  }
}
