package com.demo.drools.api.model;

import lombok.Data;

import java.util.List;

@Data
public class Request {
  private String decisionName;
  private String namespace;
  private String modelName;
  private List<Fact> facts;
}
