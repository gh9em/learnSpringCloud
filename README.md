# LearnSpringCloud

[![Gitpod ready-to-code](https://img.shields.io/badge/Gitpod-ready--to--code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/gh9em/learnSpringCloud)

# Parent Project

1. generate project template
    ```
    mvn archetype:generate 
    -DgroupId=com.learn.springcloud 
    -DartifactId=cloud-project 
    -Dversion=1.0-SNAPSHOT 
    -DarchetypeGroupId=org.apache.maven.archetypes 
    -DarchetypeArtifactId=maven-archetype-site 
    -DarchetypeVersion=RELEASE
    ```

2. delete `cloud-project/src` & modify `pom.xml`

    + add property `<packaging>pom</packaging>` (means pom parent project)
    + add plugin `spring-boot-maven-plugin`
    + provide optional sub-projects dependencies groupId&version
    + provide optional sub-projects plugins groupId&version

# Sub Project

1. generate project template
    ```
    mvn archetype:generate 
    -DgroupId=com.learn.springcloud 
    -DartifactId=cloud-provider-payment8001
    -Dversion=1.0-SNAPSHOT 
    -DarchetypeGroupId=org.apache.maven.archetypes 
    -DarchetypeArtifactId=maven-archetype-quickstart 
    -DarchetypeVersion=RELEASE
    ```
2. modify `pom.xml`

    + add dependencies

