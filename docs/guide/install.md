!!! note 

    `nuclei-plus` 有一个工作空间（目录）在第一次运行时创建，相关数据皆在其中。

## :material-tag: 发布版本

![image-20230223155726888](https://security-1254441333.cos.ap-guangzhou.myqcloud.com/knowledge-baseimage-20230223155726888.png)

`nuclei-x.x.x.jar` ：打包了完整的依赖包（平台通用）



## :octicons-file-directory-open-fill-16: 工作空间目录结构

```bash
╭─kali@G3G4X5X6-PC /mnt/c/Users/Admin/.nuclei-plus
╰─$ tree -d
.
├── bin				# 存放二进制，自动加载进 Path 变量，nuclei 可执行文件也放在这里
├── config			# 程序配置文件目录
├── projects		# 程序项目管理目录
│   ├── default		# 默认项目目录
│   │   ├── config	# 项目配置文件存储目录
│   │   ├── report	# 项目生成报告存储目录
│   │   └── temp	# 项目执行临时目录
│   └── Test		# 自定义项目
│       └── config
├── report
│   └── nuclei
├── temp
│   └── nuclei
└── templates		# 自定义模板目录（即自己写的模板可以放置在这里，程序启动自动加载）
```

> `bin` 目录中的二进制可执行文件可在内置终端中直接执行。



## :simple-openjdk: 运行条件

- JDK11+
- [projectdiscovery/nuclei](https://github.com/projectdiscovery/nuclei)
  - 需把 `nuclei` 可执行文件放置到工作空间的 `bin` 目录中。



## :fontawesome-brands-windows: Windows 安装

- 双击执行
- 使用平台通用包  `java -jar nuclei-x.x.x.jar ` 



## :fontawesome-brands-linux: Linux 安装

- 使用平台通用包  `java -jar nuclei-x.x.x.jar ` 



## :fontawesome-brands-apple: MacOS 安装

- 使用平台通用包  `java -jar nuclei-x.x.x.jar `





