package com.demo.drools.builder;

import lombok.Builder;
import lombok.Singular;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S.Tibriz on 14-3-2017. Builder class to build the Kjar.
 */
@Builder
public class KjarBuilder {

  @Builder.Default
  private static final String KJAR_FILE_EXTENSION = ".dmn";
  @Builder.Default
  private static final String KJAR_RESOURCES_PATH = "src/main/resources/";
  @Builder.Default
  private static final String INCEPTION_PROFILE_PROPERTY = "org.kie.dmn.profiles.inception";

  private ReleaseId releaseId;
  private String pomText;
  @Singular
  private List<String> sttrContents;
  private String kieSessionName;
  private boolean enableLogging;

  @Builder.Default
  private final KieServices kieServices = KieServices.Factory.get();


  public ReleaseId getReleaseId() {
    return releaseId;
  }

  public String getPomText() {
    return pomText;
  }

  public String getKjarFileName() {
    return releaseId.getArtifactId() + KJAR_FILE_EXTENSION;
  }

  public String getKieSessionName() {
    return kieSessionName;
  }

  public boolean isEnableLogging() {
    return enableLogging;
  }

  /**
   * Builds the InternalKieModule
   *
   * @return The InternalKieModule object.
   */
  public InternalKieModule createInternalKieModule() {
    KieFileSystem kieFileSystem = createKieModuleFiles();

    KieBuilder kiebuilder = kieServices.newKieBuilder(kieFileSystem);

    kiebuilder.buildAll();

    InternalKieModule result = (InternalKieModule) kieServices.getRepository()
        .getKieModule(releaseId);

    // Immediately remove the kiemodule from the repository again, since it consumes a lot of memory
    // that does not get GC'ed, eventually resulting in OutOfMemoryErrors
    kieServices.getRepository().removeKieModule(releaseId);

    return result;
  }

  /**
   * Creates the KieFileSystem. This method generates the KieFileSystem based on the releaseID and
   * the dmn content.
   *
   * @return KieFileSystem - The kie module file system
   */
  private KieFileSystem createKieModuleFiles() {
    KieModuleModel kieModuleModel = kieServices.newKieModuleModel();
    KieBaseModel kieBaseModel = createKieBaseModel(kieModuleModel);

    KieSessionModel kieSessionModel = kieBaseModel.newKieSessionModel(getKieSessionName())
        .setType(KieSessionModel.KieSessionType.STATEFUL)
        .setClockType(ClockTypeOption.get("realtime"));
    if (isEnableLogging()) {
      kieSessionModel.setFileLogger("drools-logger", 60000, true);
    }

    KieFileSystem kieFileSystem = kieServices.newKieFileSystem()
        .writeKModuleXML(kieModuleModel.toXML())
        .writePomXML(getPomText());

    for (int i = 0; i < sttrContents.size(); i++) {
      String dmnContent = sttrContents.get(i);
      String fileName = i + "_" + getKjarFileName();
      kieFileSystem.write(
          KJAR_RESOURCES_PATH + convertPackageToDirectoryStructure(getReleaseId().getGroupId())
              + "/"
              + fileName, dmnContent);
    }

    return kieFileSystem;
  }

  private KieBaseModel createKieBaseModel(KieModuleModel kieModuleModel) {
    return kieModuleModel.newKieBaseModel(getReleaseId().getArtifactId())
        .addPackage(getReleaseId().getGroupId())
        .setEqualsBehavior(EqualityBehaviorOption.EQUALITY)
        .setEventProcessingMode(EventProcessingOption.CLOUD);
  }

  /**
   * Converts the groupID string to a formatted folder structure.
   *
   * @param groupId The group id.
   * @return The transformed folder structure.
   */
  private String convertPackageToDirectoryStructure(String groupId) {
    return groupId.replace(".", "/").trim();
  }
}
