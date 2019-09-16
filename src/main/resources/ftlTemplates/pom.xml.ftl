<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>${releaseId.groupId}</groupId>
    <artifactId>${releaseId.artifactId}</artifactId>
    <version>${releaseId.version}</version>
    <repositories>
        <repository>
            <id>redhat-repo</id>
            <name>redhat-drools-repo</name>
            <url>https://maven.repository.redhat.com/ga/</url>
        </repository>
    </repositories>
    <distributionManagement>
        <repository>
            <id>${repoId}</id>
            <url>${repoUrl}</url>
        </repository>
    </distributionManagement>
</project>