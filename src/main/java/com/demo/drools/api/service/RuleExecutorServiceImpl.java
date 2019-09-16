package com.demo.drools.api.service;

import static com.demo.drools.api.util.Utils.getKieSessionName;

import com.demo.drools.api.model.Fact;
import com.demo.drools.api.model.GegevensType;
import com.demo.drools.api.model.Request;
import com.demo.drools.rule.ContainerGenerator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Created by S.Tibriz on 10-09-2019.
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RuleExecutorServiceImpl implements RuleExecutorService {

  private static final String DATE_PATTERN = "dd-MM-yyyy";
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

  @NonNull
  private final ContainerGenerator containerGenerator;

  @Value("${kjar.groupId}")
  private String kjarGroupId;

  @Override
  public Object getDecisionFromDmn(Request request, String artifact, String version) {
    log.info("Performing drools logic to retrieve decision");
    String decisionName = request.getDecisionName();
    DMNRuntime dmnRuntime = createDMNRuntime(
        getKieContainer(KieServices.Factory.get().newReleaseId(kjarGroupId, artifact, version)),
        getKieSessionName(artifact));
    DMNModel dmnModel = getDmnModel(dmnRuntime, request.getModelName(), request.getNamespace());
    if (!decisionsExists(dmnModel, decisionName)) {
      throw new RuntimeException("Decision not found.");
    }
    DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, getDmnContext(request.getFacts()));
    DMNContext resultContext = dmnResult.getContext();
    return resultContext.get(decisionName);
  }

  private DMNContext getDmnContext(List<Fact> facts) {
    DMNContext dmnContext = DMNFactory.newContext();
    facts.forEach(fact -> dmnContext.set(fact.getInputData(), convertAnswer(fact)));
    return dmnContext;
  }

  private Object convertAnswer(Fact fact) {
    String answerText = fact.getAnswer();
    GegevensType answerType = fact.getAnswerType();
    if (StringUtils.isEmpty(answerText)) {
      return null;
    }
    Object answerObject;
    switch (answerType) {
      case BOOLEAN:
        answerObject = Boolean.valueOf(answerText);
        break;
      case DATE:
        answerObject = extractDate(answerText);
        break;
      case NUMBER:
        answerObject = extractBigDecimal(answerText);
        break;
      case STRING:
      case LIST:
        answerObject = answerText;
        break;
      default:
        answerObject = null;
        break;
    }
    return answerObject;
  }

  private LocalDate extractDate(String date) {
    try {
      return LocalDate.parse(date, FORMATTER);
    } catch (DateTimeParseException e) {
      throw new RuntimeException(e.getLocalizedMessage(), e);
    }
  }

  private BigDecimal extractBigDecimal(String number) {
    try {
      return new BigDecimal(number);
    } catch (NumberFormatException e) {
      throw new RuntimeException(e.getLocalizedMessage(), e);
    }
  }

  private KieContainer getKieContainer(ReleaseId releaseId) {
    return containerGenerator
        .getContainer(releaseId.getGroupId(), releaseId.getArtifactId(), releaseId.getVersion());
  }

  private DMNRuntime createDMNRuntime(KieContainer kieContainer, String sessionName) {
    KieSession kieSession = kieContainer.newKieSession(sessionName);
    if (kieSession == null) {
      throw new RuntimeException("No active session found.");
    }
    DMNRuntime result = kieSession.getKieRuntime(DMNRuntime.class);
    kieSession.destroy();
    return result;
  }

  private boolean decisionsExists(DMNModel model, String decisionNames) {
    return model.getDecisionByName(decisionNames) != null;
  }

  private DMNModel getDmnModel(DMNRuntime dmnRuntime, String dmnModelName, String dmnNamespace) {
    DMNModel dmnModel;
    try {
      dmnModel = dmnRuntime.getModel(dmnNamespace, dmnModelName);
    } catch (Exception e) {
      throw new RuntimeException(e.getLocalizedMessage(), e);
    }
    if (dmnModel == null) {
      throw new RuntimeException("Empty dmn model.");
    }
    return dmnModel;
  }

}
