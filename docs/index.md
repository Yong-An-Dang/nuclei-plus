# 欢迎使用 `nuclei-plus`

Nuclei 用于基于模板跨目标发送请求，从而实现零误报并提供对大量主机的快速扫描。 Nuclei 提供对各种协议的扫描，包括 TCP、DNS、HTTP、SSL、File、Whois、Websocket、Headless 等。凭借强大而灵活的模板，Nuclei 可用于对各种安全检查进行建模。

> 开源不易，且行且珍惜！



!!! note

    `nuclei-plus` 基于 nuclei 的概念验证框架GUI
    
    **注意：以下文档尚未更新**



## :material-security: 安全通告

See [the security file](https://github.com/G3G4X5X6/ultimate-cube/security/policy)!

## :material-checkbox-multiple-marked: 功能特点

- [x] 支持多操作系统平台，兼容性测试：`Windows` > `Linux` > `MacOS`
- [x] 支持会话管理
- [x] 支持本地终端(cmd, bash)
- [x] 支持 `SSH`、 `Sftp`，及` 内置代码编辑器`，支持代码高亮、折叠等功能
- [x] 支持  `COM`  口调试（自动检测存在的 `COM` 接口）
- [x] 支持 `Telnet`
- [x] 支持 `RDP` 远程桌面（基于`FreeRDP` 实现） 
- [x] 支持 `VNC`，基于`TightVNC Viewer` 实现
- [x] 支持 `集成外部工具`，实现快速启动
- [x] 内置 `简易编辑器` ，可编辑本地、远程文本文件
- [x] <del>内置 `Nuclei` GUI，POC概念验证框架（已独立项目）</del>
- [x] 支持60多种主题皮肤切换
- [ ] 支持插件系统

## :fontawesome-brands-guilded: 项目构建

- 开发JDK版本要求：JDK 11+

- 安装依赖库到本地仓库 

  ```shel
  # tightvnc-jviewer.jar, jediterm-pty-2.66.jar, terminal-2.66.jar, jediterm-typeahead-2.66.jar
  mvn install:install-file -Dfile=libs/tightvnc-jviewer.jar -DgroupId=com.g3g4x5x6  -DartifactId=tightvnc-jviewer -Dversion=2.8.3 -Dpackaging=jar
  mvn install:install-file -Dfile=libs/jediterm-typeahead-2.66.jar -DgroupId=com.g3g4x5x6  -DartifactId=jediterm-typeahead -Dversion=2.66 -Dpackaging=jar
  mvn install:install-file -Dfile=libs/terminal-2.66.jar -DgroupId=com.g3g4x5x6  -DartifactId=terminal -Dversion=2.66 -Dpackaging=jar
  mvn install:install-file -Dfile=libs/jediterm-pty-2.66.jar -DgroupId=com.g3g4x5x6  -DartifactId=jediterm-pty -Dversion=2.66 -Dpackaging=jar
  ```

  

## :material-download: 下载安装

1. 跨平台运行文件： `jar`
1. Windows平台安装包：`exe`
1. 其他平台暂无安装包，请使用 `jar` 包，[去下载](https://github.com/G3G4X5X6/ultimate-cube/releases)



## :material-file-document-multiple: 使用指南

[ultimate-cube 使用指南](guide/index.md)



## :octicons-package-dependents-16: 依赖库

- JediTerm: [https://github.com/JetBrains/jediterm](https://github.com/JetBrains/jediterm)
- FlatLaf: [https://github.com/JFormDesigner/FlatLaf](https://github.com/JFormDesigner/FlatLaf)
- Apache MINA SSHD: [https://github.com/apache/mina-sshd](https://github.com/apache/mina-sshd)
- RSyntaxTextArea: [https://github.com/bobbylight/RSyntaxTextArea](https://github.com/bobbylight/RSyntaxTextArea)
- More...



## :man_supervillain: 维护者

[@G3G4X5X6](https://github.com/G3G4X5X6)



## :people_holding_hands: 贡献者

See [contributors](https://github.com/G3G4X5X6/ultimate-cube/graphs/contributors)!

PRs accepted.



## :books: 授权许可

MIT © 2022 勾三股四弦五小六



## :star_struck: 集星趋势 (Stared)

![Stargazers over time](https://starchart.cc/G3G4X5X6/ultimateshell.svg)



## :technologist: 技术支持（社区支持）

Having trouble with Pages? Check out our [wiki](https://github.com/G3G4X5X6/ultimateshell/wiki) or [Discussions for support](https://github.com/G3G4X5X6/ultimateshell/discussions) and we’ll help you sort it out.







