package com.demo.drools.api.service;

import org.kie.api.builder.ReleaseId;

/**
 * Created by S.Tibriz on 14-3-2017. Service to create different type of data using provided
 * template.
 */
public interface TemplateService {

  /**
   * Create pom.xml content for the provided parameters
   *
   * @param releaseId the release-id of the pom
   * @param repoUrl the maven repository url to be included in the pom
   * @param repoId The maven repository Id to be  included in the pom
   * @return content of the pom.xml
   */
  String createPom(ReleaseId releaseId, String repoUrl, String repoId);

  /**
   * Create a maven settings.xml with the provided username, password and repoUrl
   *
   * @param properties object containing all required properties to create maven settings: -
   * username Username of the maven repository - password password of the maven repository. -
   * repoUrl the maven repository url to be included in the pom - localRepo location  of the local
   * maven repository where  artifacts are cached - repoId The maven repository Id to be  included
   * in the pom
   * @return content of the maven settings.xml
   */
  String createMavenSettings(MavenSettingsProperties properties);
}
