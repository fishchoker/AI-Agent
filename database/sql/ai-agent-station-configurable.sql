-- AI Agent Station 配置化改造 - 新增表结构
-- 创建时间: 2025-01-XX
-- 说明: 用数据库表替代代码中的硬编码流程

-- 1. AI智能体组装流程配置表
CREATE TABLE `ai_agent_assembly_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `agent_id` bigint(20) NOT NULL COMMENT '智能体ID',
  `assembly_type` varchar(50) NOT NULL COMMENT '组装类型(chat_client/model/advisor/tool)',
  `assembly_order` int(11) NOT NULL COMMENT '组装顺序',
  `assembly_config` text COMMENT '组装配置JSON',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_agent_assembly` (`agent_id`, `assembly_type`, `assembly_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI智能体组装流程配置表';

-- 2. 系统提示词模板表
CREATE TABLE `ai_system_prompt_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `template_type` varchar(50) NOT NULL COMMENT '模板类型(default/rag/custom)',
  `template_content` text NOT NULL COMMENT '模板内容',
  `template_variables` text COMMENT '模板变量配置JSON',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_name` (`template_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统提示词模板表';

-- 3. 顾问创建策略配置表
CREATE TABLE `ai_advisor_strategy_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `advisor_type` varchar(50) NOT NULL COMMENT '顾问类型',
  `strategy_class` varchar(200) NOT NULL COMMENT '策略实现类',
  `strategy_config` text COMMENT '策略配置JSON',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_advisor_type` (`advisor_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='顾问创建策略配置表';

-- 4. 工具回调提供者配置表
CREATE TABLE `ai_tool_callback_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `callback_type` varchar(50) NOT NULL COMMENT '回调类型(mcp/function_call)',
  `provider_class` varchar(200) NOT NULL COMMENT '提供者实现类',
  `provider_config` text COMMENT '提供者配置JSON',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_callback_type` (`callback_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具回调提供者配置表';
