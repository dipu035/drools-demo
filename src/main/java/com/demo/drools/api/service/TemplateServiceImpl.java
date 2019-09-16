package com.demo.drools.api.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.builder.ReleaseId;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by S.Tibriz on 14-3-2017. Implementation class for the TemplateService interface.
 */
@Component
@Slf4j
public class TemplateServiceImpl implements TemplateService {

  private static final String TEMPLATE_FOLDER = "ftlTemplates";
  private static final String POM_XML_TEMPLATE = "pom.xml.ftl";
  private static final String SETTINGS_XML_TEMPLATE = "settings.xml.ftl";

  private Configuration configuration;

  public TemplateServiceImpl() {
    configuration = new Configuration(Configuration.VERSION_2_3_25);
    configuration
        .setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), TEMPLATE_FOLDER);
    configuration.setDefaultEncoding("UTF-8");
    configuration.setLocale(Locale.US);
    configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
  }

  @Override
  public String createPom(final ReleaseId releaseId, String repoUrl, String repoId) {
    Map<String, Object> data = new HashMap<>();
    data.put("releaseId", releaseId);
    data.put("repoUrl", repoUrl);
    data.put("repoId", repoId);
    return processTemplate(POM_XML_TEMPLATE, data);
  }

  @Override
  public String createMavenSettings(MavenSettingsProperties properties) {
    Map<String, Object> data = new HashMap<>();
    data.put("username", properties.getUsername());
    data.put("password", properties.getPassword());
    data.put("repoUrl", properties.getRepoUrl());
    data.put("repoId", properties.getRepoId());
    data.put("localRepo", properties.getLocalRepo());
    return processTemplate(SETTINGS_XML_TEMPLATE, data);
  }

  private String processTemplate(final String templateName,
      final Map<String, Object> templateData) {
    try {
      Template template = configuration.getTemplate(templateName);
      StringWriter output = new StringWriter();
      template.process(templateData, output);
      return output.toString();
    } catch (IOException | TemplateException e) {
      throw new RuntimeException("Failed to read template", e);
    }
  }
}
