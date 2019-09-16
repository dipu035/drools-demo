package com.demo.drools.api.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MavenSettingsProperties {

  String username;
  String password;
  String repoUrl;
  String localRepo;
  String repoId;
}
