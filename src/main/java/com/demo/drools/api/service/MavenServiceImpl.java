package com.demo.drools.api.service;

import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.settings.Server;
import org.appformer.maven.integration.embedder.MavenEmbedder;
import org.appformer.maven.integration.embedder.MavenProjectLoader;
import org.appformer.maven.integration.embedder.MavenSettings;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.RemoteRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.scanner.KieMavenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MavenServiceImpl implements MavenService {

  private static final String FILENAME_SETTINGS = "settings.xml";
  private static final String FILENAME_POM = "pom.xml";
  private static final String PROPERTY_LOCAL_REPO = "localRepoUrl";
  private static final String PROPERTY_REPO_USERNAME = "repoUsername";
  private static final String PROPERTY_REPO_PASSWORD = "repoPassword";

  @NonNull
  private TemplateService templateService;

  @NonNull
  private Environment env;

  @Value("${repository.id}")
  private String repoId;

  @Value("${repoUrl}")
  private String repoUrl;

  private KieMavenRepository repository;

  @PostConstruct
  public void init() {
    String repoUsername = env.getProperty(PROPERTY_REPO_USERNAME);
    String repoPassword = env.getProperty(PROPERTY_REPO_PASSWORD);
    String localRepo = env.getProperty(PROPERTY_LOCAL_REPO);
    MavenSettingsProperties properties = new MavenSettingsProperties(repoUsername, repoPassword,
        repoUrl, localRepo, repoId);
    String settingsContent = templateService.createMavenSettings(properties);
    Path settingsPath = createTempFile(FILENAME_SETTINGS, settingsContent);
    System.setProperty(MavenSettings.CUSTOM_SETTINGS_PROPERTY, settingsPath.toString());
    MavenSettings.reinitSettings();
    repository = getRepository();
    log.info("MavenService initialized with localRepo {} and remoteRepo {}", localRepo, repoUrl);
  }

  @Override
  public void deployArtifact(ReleaseId releaseId, InternalKieModule kieModule, String pomContent) {
    Path pomPath = createTempFile(FILENAME_POM, pomContent);
    try {
      repository.deployArtifact(getRemoteRepository(), releaseId, kieModule, pomPath.toFile());
    } catch (Exception e) {
      throw new RuntimeException("Failed to deploy artifact", e);
    }
  }

  @Override
  public String getRepoUrl() {
    return repoUrl;
  }

  @Override
  public boolean isArtifactExistsInRepository(ReleaseId releaseId) {
    Artifact artifact = repository.resolveArtifact(releaseId);
    return artifact != null;
  }

  private KieMavenRepository getRepository() {
    return getKieMavenRepository();
  }

  private RemoteRepository getRemoteRepository() {
    RemoteRepository.Builder remoteRepoBuilder = new RemoteRepository.Builder(repoId, "default",
        repoUrl);
    Server server = MavenSettings.getSettings().getServer(repoId);
    if (server != null) {
      MavenEmbedder embedder = MavenProjectLoader.newMavenEmbedder(false);
      try {
        Authentication authentication = embedder.getMavenSession().getRepositorySession()
            .getAuthenticationSelector().getAuthentication(remoteRepoBuilder.build());
        remoteRepoBuilder.setAuthentication(authentication);
      } finally {
        embedder.dispose();
      }
    }

    return remoteRepoBuilder.build();
  }

  private Path createTempFile(String fileName, String content) {

    try {
      Path filePath = Files.createTempFile(fileName, null);
      Files.write(filePath, content.getBytes());
      return filePath;
    } catch (IOException e) {
      throw new RuntimeException("Faild to create temp file.", e);
    }
  }
}
