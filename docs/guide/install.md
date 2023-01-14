!!! note 

    `nuclei-plus` 有一个工作空间（目录）在第一次运行时创建，相关数据皆在其中。
    
    **注意：以下文档尚未更新**

## :material-tag: 发布版本

![image-20230103142236248](https://security-1254441333.cos.ap-guangzhou.myqcloud.com/knowledge-baseimage-20230103142236248.png)

1. `ultimate-app-x.x.x-jar-with-dependencies.jar` ：打包了完整的依赖包（平台通用）
3. `ultimatecube_setup.exe`：Windows安装程序（Windows专用）


## :fontawesome-brands-windows: Windows 安装

:one: Windows `ultimatecube_setup.exe` 安装包

不会吧，不会吧，不会还有人不懂双击Windows安装程序安装软件吧。:zany_face:

:two: winget

```bash
# search
winget search ultimate-cube

# install
winget install ultimate-cube
```

![img_5](https://security-1254441333.cos.ap-guangzhou.myqcloud.com/knowledge-baseimg_5.png)



:three: 通用包 `ultimate-app-x.x.x-jar-with-dependencies.jar`  

- 已安装 `JDK`，并配置了系统环境变量（不懂的建议百度），可 `双击执行`，或者命令执行 `java -jar ultimate-app-x.x.x-jar-with-dependencies.jar ` 
- 没有配置环境变量的可以使用 `java` 程序的绝对路径，`D:\jdk-11\bin\java.exe -jar java -jar ultimate-app-x.x.x-jar-with-dependencies.jar`



## :fontawesome-brands-linux: Linux 安装

建议使用平台通用包  `java -jar ultimate-app-x.x.x-jar-with-dependencies.jar ` 





## :fontawesome-brands-apple: MacOS 安装

建议使用平台通用包  `java -jar ultimate-app-x.x.x-jar-with-dependencies.jar ` 







