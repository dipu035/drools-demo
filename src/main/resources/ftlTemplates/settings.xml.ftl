<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
          http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>${repoId}</id>
        <#if username?has_content>
            <username>${username}</username>
        </#if>
        <#if password?has_content>
            <password>${password}</password>
        </#if>
    </server>
  </servers>
  <profiles>
    <profile>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <repositories>
        <repository>
          <id>test</id>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
            <checksumPolicy>fail</checksumPolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
            <checksumPolicy>fail</checksumPolicy>
          </snapshots>
          <url>${repoUrl}</url>
        </repository>
      </repositories>
    </profile>
  </profiles>
    <#if localRepo?has_content>
        <localRepository>${localRepo}</localRepository>
    </#if>
</settings>