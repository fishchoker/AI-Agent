package cn.bugstack.ai.domain.agent.service.strategy;

import cn.bugstack.ai.domain.agent.model.valobj.AdvisorStrategyVO;
import cn.bugstack.ai.domain.agent.adapter.repository.IAgentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 顾问策略服务
 * 根据配置动态创建不同类型的Advisor
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@Slf4j
@Service
public class AdvisorStrategyService {
    
    @Resource
    private IAgentRepository repository;

    /**
     * 根据策略配置创建Advisor
     * 替代硬编码的Advisor创建逻辑
     */
    public Advisor createAdvisor(AdvisorStrategyVO strategy, Map<String, Object> config) {
        try {
            // 使用反射创建Advisor实例
            Class<?> advisorClass = Class.forName(strategy.getStrategyClass());
            Object advisor = advisorClass.getDeclaredConstructor().newInstance();
            
            // 根据配置设置参数
            setAdvisorConfig(advisor, strategy.getStrategyConfig(), config);
            
            log.info("成功创建Advisor，类型: {}, 策略类: {}", strategy.getAdvisorType(), strategy.getStrategyClass());
            return (Advisor) advisor;
            
        } catch (Exception e) {
            log.error("创建Advisor失败，类型: {}, 策略类: {}", strategy.getAdvisorType(), strategy.getStrategyClass(), e);
            return null;
        }
    }
    
    /**
     * 设置Advisor配置
     * 根据不同的Advisor类型设置不同的配置
     */
    private void setAdvisorConfig(Object advisor, String strategyConfig, Map<String, Object> config) {
        // 根据不同的Advisor类型设置不同的配置
        // 这里可以使用策略模式进一步优化
        
        try {
            // 解析策略配置JSON
            if (strategyConfig != null && !strategyConfig.isEmpty()) {
                // 使用JSON解析配置并设置到advisor对象中
                // 具体实现需要根据不同的Advisor类型来处理
                log.info("设置Advisor配置: {}", strategyConfig);
            }
        } catch (Exception e) {
            log.error("设置Advisor配置失败", e);
        }
    }
    
    /**
     * 根据顾问类型创建Advisor
     */
    public Advisor createAdvisorByType(String advisorType, Map<String, Object> config) {
        // 从数据库查询顾问策略配置
        AdvisorStrategyVO strategy = repository.queryAdvisorStrategyByType(advisorType);
        if (strategy != null) {
            return createAdvisor(strategy, config);
        }
        
        log.warn("未找到顾问策略配置: {}", advisorType);
        return null;
    }
}
