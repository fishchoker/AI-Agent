# AI Agent Station 配置化改造

## 概述
本文件夹包含AI Agent Station项目的配置化改造代码，用数据库表替代代码中的硬编码流程。

## 文件夹结构
```
ai-agent-station-configurable/
├── database/                    # 数据库相关文件
│   ├── sql/                    # SQL脚本
│   └── README.md               # 数据库说明
├── domain/                     # 领域层文件
│   ├── service/               # 服务类
│   ├── model/                 # 模型类
│   └── README.md              # 领域层说明
├── infrastructure/            # 基础设施层文件
│   ├── dao/                   # 数据访问对象
│   └── README.md              # 基础设施层说明
├── mapper/                    # MyBatis映射文件
│   └── README.md              # 映射文件说明
├── config/                    # 配置文件
└── REPLACED_FILES.md          # 被替代的原文件列表
```

## 被替代的原文件
详见 [REPLACED_FILES.md](./REPLACED_FILES.md)

## 配置化改造要点
1. 新增4个关键数据库表
2. 创建配置化服务类
3. 修改现有组装逻辑
4. 支持动态配置管理

## 使用说明
1. 先执行数据库脚本创建新表
2. 插入初始配置数据
3. 替换原项目中的对应文件
4. 重启应用测试配置化功能
