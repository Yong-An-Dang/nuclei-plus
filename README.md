<div align=center style="margin-top: 10px;">

![nuclei-plus-icon](doc/images/icon.png)
<h1>nuclei-plus</h1>
</div>

Nuclei is used to send requests across targets based on a template, leading to zero false positives and providing fast
scanning on a large number of hosts. Nuclei offers scanning for a variety of protocols, including TCP, DNS, HTTP, SSL,
File, Whois, Websocket, Headless etc. With powerful and flexible templating, Nuclei can be used to model all kinds of
security checks.
> <small>[简体中文](README_zh.md)</small>

#### Intro

Functional enhancement based on nuclei

#### Support

- [x] Support system tray
- [x] Support project management
- [x] Support configuration management
- [x] Support template management
- [x] Support template editing
- [x] Support internationalization, default `zh_CN`
- [x] Support multiple network space engine interface search

#### Build

```shell
# JDK11+

# add terminal
mvn install:install-file -Dfile=libs/jediterm-core-3.20-SNAPSHOT.jar -DgroupId=com.jediterm  -DartifactId=jediterm-core -Dversion=3.20-SNAPSHOT -Dpackaging=jar
mvn install:install-file -Dfile=libs/jediterm-ui-3.20-SNAPSHOT.jar -DgroupId=com.jediterm  -DartifactId=jediterm-ui -Dversion=3.20-SNAPSHOT -Dpackaging=jar

# set a new version
mvn versions:set -DnewVersion=7.1.4

# package
mvn clean package -DskipTests
```


#### Run

```shell
# JDK11+
java -jar nuclei-x.x.x.jar
```

#### Usage

See [Document](https://yong-an-dang.github.io/nuclei-plus/)

#### Dependency

- [projectdiscovery/nuclei](https://github.com/projectdiscovery/nuclei)
- [JetBrains/jediterm](https://github.com/JetBrains/jediterm)
- [JFormDesigner/FlatLaf](https://github.com/JFormDesigner/FlatLaf)
- more...

#### [Thanks to `JetBrains` for their support](https://jb.gg/OpenSourceSupport)

![JetBrains Logo (Main) logo](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg)