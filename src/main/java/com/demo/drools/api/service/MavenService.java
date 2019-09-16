package com.demo.drools.api.service;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.api.builder.ReleaseId;

/**
 * Service to comminicate with the maven repository
 *
 */
public interface MavenService {

  /**
   * Deploy artifact to maven repository.
   *
   * @param releaseId the releaseId containing the groupid, artifactid and version of the artificat
   * @param kieModule the kieModule to be stored as artifact
   * @param pomContent the content of the pom.xml describing the artifact
   */
  void deployArtifact(ReleaseId releaseId, InternalKieModule kieModule, String pomContent);


  /**
   * Retrieve the url of the maven repository.
   *
   * @return the repository url
   */
  String getRepoUrl();

  /**
   * Checks in the maven repository if the provided artifact is already exists.
   *
   * @param releaseId The release id of the artifact.
   * @return <code>true</code> if the artifact is available in repository, otherwise
   * <code>false</code>
   */
  boolean isArtifactExistsInRepository(ReleaseId releaseId);
}
