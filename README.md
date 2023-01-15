<div align=center style="margin-top: 10px;">

![nuclei-plus-icon](doc/images/icon.png)
<h1>nuclei-plus</h1>
</div>

Nuclei is used to send requests across targets based on a template, leading to zero false positives and providing fast scanning on a large number of hosts. Nuclei offers scanning for a variety of protocols, including TCP, DNS, HTTP, SSL, File, Whois, Websocket, Headless etc. With powerful and flexible templating, Nuclei can be used to model all kinds of security checks.

#### Intro
> [简体中文](README_zh.md)

Functional enhancement based on nuclei

#### Todo
- [x] Support system tray
- [ ] Support project management
- [ ] Support configuration management
- [ ] Support template management
- [ ] Support template editing
- [ ] Support internationalization, default `zh`
- [ ] Support multiple network space engine interface search

#### Build
```shell
mvn clean package -DskipTests
```

#### Run
```shell
# jdk11+
java -jar nuclei-x.x.x.jar
```

#### Usage
See [doc](https://yong-an-dang.github.io/nuclei-plus/)