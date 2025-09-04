package cn.bugstack.ai.domain.agent.model.valobj;

import lombok.Data;

/**
 * 顾问策略值对象
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@Data
public class AdvisorStrategyVO {
    
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
}
