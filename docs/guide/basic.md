# 基础

## 菜单栏

![image-20230224144708792](https://security-1254441333.cos.ap-guangzhou.myqcloud.com/knowledge-baseimage-20230224144708792.png)



## 工具栏

![image-20230224144900486](https://security-1254441333.cos.ap-guangzhou.myqcloud.com/knowledge-baseimage-20230224144900486.png)

从左往右依次为：

- 新建终端或者右键选择终端，执行当前活动配置
- run only new templates added in latest nuclei-templates release
- automatic web scan using wappalyzer technology detection to tags mapping
- 右键选择自定义分组的模板，执行当前活动配置
- 保存当前项目的所有执行配置
- 右键选择配置当前活动配置（本工具支持多配置管理，活动配置为当前默认执行的配置）



## 模板管理

![image-20230224145528793](https://security-1254441333.cos.ap-guangzhou.myqcloud.com/knowledge-baseimage-20230224145528793.png)

模板管理面板主要有三块：

- 工具栏，实现了模板危险等级的过滤筛选以及关键字搜索功能
- 模板表格，列出了模板的基本信息
- 右键菜单，实现模板的各种应用、配置和管理



## 目标管理

![image-20230224150054937](https://security-1254441333.cos.ap-guangzhou.myqcloud.com/knowledge-baseimage-20230224150054937.png)

这里实现了目标从文件中的加载和保存。





## 配置管理

![image-20230224150216718](https://security-1254441333.cos.ap-guangzhou.myqcloud.com/knowledge-baseimage-20230224150216718.png)

配置管理提供了多配置管理的能力，点击 `加号` 图标可以新增配置，每个配置主要有三块内容：

- `template ` 配置，可在模板管理面板中右键追加配置
- `workflow` 配置，可在模板管理面板中右键追加配置
- 还有除此之外的 `nuclei` 配置，这里不再配置 `template` 和 `workflow`



## 运行终端

![image-20230224150729462](https://security-1254441333.cos.ap-guangzhou.myqcloud.com/knowledge-baseimage-20230224150729462.png)

工具内置了终端，用于执行验证和其他命令（在 `bin` 目录下的二进制命令工具）

- 点击 `加号` 新增终端
- 可以刷新终端
- 终端自动加载 `bin` 目录到 `PATH` 环境变量中



## 网络空间搜索引擎

![image-20230224151233440](https://security-1254441333.cos.ap-guangzhou.myqcloud.com/knowledge-baseimage-20230224151233440.png)

目前内置了两款网络空间搜索引擎：`Fofa` 和 `Hunter`

右键菜单功能：

- 浏览器打开目标
- 追加到待测试的全局目标面板
- 直接使用自定义分组的模板对选中的目标进行测试



