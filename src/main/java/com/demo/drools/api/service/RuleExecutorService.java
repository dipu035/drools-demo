package com.demo.drools.api.service;

import com.demo.drools.api.model.Request;

/**
 * Created by S.Tibriz on 10-09-2019.
 */
public interface RuleExecutorService {

  /**
   * Get a decision for the provided decissionName based on the provided answers
   */
  Object getDecisionFromDmn(Request request, String artifact, String version);
}
