package cn.bugstack.ai.infrastructure.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 顾问创建策略配置PO
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@Data
@TableName("ai_advisor_strategy_config")
public class AdvisorStrategyConfig {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 顾问类型
     */
    private String advisorType;
    
    /**
     * 策略实现类
     */
    private String strategyClass;
    
    /**
     * 策略配置JSON
     */
    private String strategyConfig;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 状态(0:禁用,1:启用)
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}
