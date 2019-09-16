package com.demo.drools.rule;

import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;

/**
 * Created by S.Tibriz on 20-3-2017. Creates new KieContainer if not exists already, if exists
 * returns the existing KieContainer
 */
@Component
@Slf4j
public class ContainerGenerator {

  private KieServices kieServices;
  protected Map<ReleaseId, KieContainer> kieContainerCache = new ConcurrentHashMap<>();

  @PostConstruct
  private void init() {
    kieServices = KieServices.Factory.get();
  }

  /**
   * Creates a new KieContainer using the ReleaseID provided by the method parameter
   *
   * @param releaseId The releaseID.
   * @return The resulting KieContainer object.
   */
  private KieContainer generateContainer(final ReleaseId releaseId) {
    try {
      return kieServices.newKieContainer(releaseId);
    } catch (Exception e) {
      // When the artifact does not exist in the repository, the above code throws a RuntimeException
      // (I was hoping for a bit more specific exception, but alas).
      throw new RuntimeException("Failed to retrieve artifact.", e);
    }
  }

  /**
   * Takes the ReleaseId as input parameter and returns the corresponding KieContainer. Internally,
   * containers are cached in a map to ensure a containers is generated only once.
   */
  public KieContainer getContainer(String groupId, String artifactId, String version) {
    if (groupId == null || artifactId == null || version == null) {
      throw new RuntimeException("Missing GAV");
    }
    ReleaseId releaseId = kieServices.newReleaseId(groupId, artifactId, version);
    KieContainer result = getContainer(releaseId);
    disposeOldContainers(releaseId);
    return result;
  }

  /**
   * Retrieves a container with the provided containerKey from the cache if it exists, and creates a
   * new one otherwise.
   *
   * Internally, this method relies on ConcurrentHashMap.computeIfAbsent() to ensure that the
   * container is created only once in the presence of multiple concurrent calls for the same
   * containerKey.
   *
   * Since container-generation is a long-running resource-intensive task, concurrent calls with the
   * same containerKey might be blocked for a long time waiting for the container generation to
   * complete
   *
   * @param containerKey ReleaseId identifying the container
   * @return KieContainer corresponding to the provided releaseId
   */
  private KieContainer getContainer(ReleaseId containerKey) {
    KieContainer result = kieContainerCache
        .computeIfAbsent(containerKey, k -> generateContainer(containerKey));

    if (result == null) {
      throw new RuntimeException("Container generation failed.");
    } else {
      return result;
    }
  }

  /**
   * Removes all containers from the cache that have the same artifactId and groupId as the provided
   * currentContainerKey, but a different version. It also calls KieCOntainer.dispose() on the
   * removed containers to free resources.
   *
   * Internally this method relies on ConcurrentHashMap.computeIfPresent() to ensure
   * KieContainer.dispose is only called once even when concurrent threads try to remove the same
   * containers
   */
  private void disposeOldContainers(ReleaseId currentContainerKey) {
    List<ReleaseId> oldContainerKeys = new ArrayList<>();
    for (ReleaseId cachedContainerKey : kieContainerCache.keySet()) {
      if (isSameGroupIdArtifactId(cachedContainerKey, currentContainerKey) && isDifferentVersion(
          cachedContainerKey, currentContainerKey)) {
        oldContainerKeys.add(cachedContainerKey);
      }
    }

    for (ReleaseId oldContainerKey : oldContainerKeys) {
      kieContainerCache.computeIfPresent(oldContainerKey, (releaseId, container) -> {
        container.dispose();
        return null;
      });
    }
  }

  private boolean isDifferentVersion(ReleaseId cachedReleaseId, ReleaseId releaseId) {
    return !cachedReleaseId.getVersion().equals(releaseId.getVersion());
  }

  private boolean isSameGroupIdArtifactId(ReleaseId cachedReleaseId, ReleaseId releaseId) {
    return cachedReleaseId.getArtifactId().equals(releaseId.getArtifactId())
        && cachedReleaseId.getGroupId().equals(releaseId.getGroupId());
  }
}
