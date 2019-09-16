package com.demo.drools.api.service;

import org.kie.api.builder.ReleaseId;

import java.util.List;

public interface RuleDeployService {

  /**
   * Creates and deploys a kjar for the provided dmn.
   */
  ReleaseId createAndDeployKjar(String dmnContent, String artifactId, String version);
}
