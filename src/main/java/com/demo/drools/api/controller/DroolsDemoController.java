package com.demo.drools.api.controller;

import com.demo.drools.api.model.Request;
import com.demo.drools.api.service.RuleDeployService;
import com.demo.drools.api.service.RuleExecutorService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.kie.api.builder.ReleaseId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Size;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class DroolsDemoController {

  @NonNull
  private final RuleDeployService ruleDeployService;

  @NonNull
  private final RuleExecutorService executorService;

  @PostMapping(value = "/rules/_upload/{artifactId}")
  @ResponseBody
  public ResponseEntity<String> process(@RequestBody String dmnContent,
      @PathVariable @Size(max = 100) String artifactId,
      @RequestParam(value = "version") @Size(max = 10) String version) {
    ReleaseId releaseId = ruleDeployService.createAndDeployKjar(dmnContent, artifactId, version);
    return ResponseEntity
        .ok("Rule " + releaseId.getArtifactId() + " uploaded successfully with version :"
            + releaseId.getVersion());
  }

  @PostMapping(value = "/rules/_execute/{artifactId}")
  @ResponseBody
  public ResponseEntity<String> execute(@RequestBody Request request,
      @PathVariable @Size(max = 100) String artifactId,
      @RequestParam(value = "version") @Size(max = 10) String version) {
    Object result = executorService.getDecisionFromDmn(request, artifactId, version);
    return ResponseEntity.ok(result.toString());
  }

}
