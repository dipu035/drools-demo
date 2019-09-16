package com.demo.drools.api.service;

import static com.demo.drools.api.util.Utils.getKieSessionName;

import com.demo.drools.builder.KjarBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RuleDeployServiceImpl implements RuleDeployService {

  @NonNull
  private final TemplateService templateService;
  @NonNull
  private final MavenService mavenService;

  @Value("${application.version}")
  private String inceptionDmnProfileVersion;
  @Value("${repository.id}")
  private String repoId;
  @Value("${kjar.groupId}")
  private String kjarGroupId;

  private KieServices kieServices;

  @Value("${enableDroolsLogger:false}")
  private boolean enableDroosLogger;

  @PostConstruct
  public void init() {
    kieServices = KieServices.Factory.get();
  }

  @Override
  public ReleaseId createAndDeployKjar(String dmnContent, String artifectId, String version) {
    ReleaseId releaseId = kieServices.newReleaseId(getKjarGroupId(), artifectId, version);
    String sessionName = getKieSessionName(releaseId.getArtifactId());
    String pomText = templateService.createPom(releaseId, mavenService.getRepoUrl(), repoId);
    InternalKieModule internalKieModule = KjarBuilder.builder().sttrContent(dmnContent)
        .pomText(pomText).releaseId(releaseId)
        .kieSessionName(sessionName).enableLogging(enableDroosLogger)
        .build()
        .createInternalKieModule();
    mavenService.deployArtifact(releaseId, internalKieModule, pomText);
    return releaseId;
  }

  private String getKjarGroupId() {
    return this.kjarGroupId;
  }
}
