package cn.bugstack.ai.config;

import cn.bugstack.ai.domain.agent.model.entity.ArmoryCommandEntity;
import cn.bugstack.ai.domain.agent.model.valobj.enums.AiAgentEnumVO;
import cn.bugstack.ai.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import cn.bugstack.ai.infrastructure.dao.IAiClientDao;
import cn.bugstack.ai.infrastructure.dao.po.AiClient;
import cn.bugstack.ai.types.common.Constants;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI Agent 自动装配配置类
 * 在Spring Boot应用启动完成后，根据配置自动装配AI客户端
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/1/15 10:00
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(AiAgentAutoConfigProperties.class)
@ConditionalOnProperty(prefix = "spring.ai.agent.auto-config", name = "enabled", havingValue = "true")
public class AiAgentAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    @Resource
    private AiAgentAutoConfigProperties aiAgentAutoConfigProperties;

    @Resource
    private DefaultArmoryStrategyFactory defaultArmoryStrategyFactory;

    @Resource
    private IAiClientDao aiClientDao;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            log.info("AI Agent 自动装配开始，配置: {}", aiAgentAutoConfigProperties);
            
            // 检查配置是否有效
            if (!aiAgentAutoConfigProperties.isEnabled()) {
                log.info("AI Agent 自动装配未启用");
                return;
            }

            // 优先从数据库查询所有启用的客户端
            List<AiClient> enabledClients = aiClientDao.queryEnabledClients();
            List<String> commandIdList;
            
            if (!CollectionUtils.isEmpty(enabledClients)) {
                // 从数据库获取启用的客户端ID列表
                commandIdList = enabledClients.stream()
                        .map(AiClient::getClientId)
                        .filter(clientId -> clientId != null && !clientId.trim().isEmpty())
                        .collect(Collectors.toList());
                log.info("从数据库查询到 {} 个启用的客户端进行预热", commandIdList.size());
            } else {
                // 如果数据库中没有启用的客户端，则回退到配置文件
                List<String> clientIds = aiAgentAutoConfigProperties.getClientIds();
                if (CollectionUtils.isEmpty(clientIds)) {
                    log.warn("数据库中没有启用的客户端，且配置文件中也没有指定客户端ID列表");
                    return;
                }
                
                // 解析客户端ID列表（支持逗号分隔的字符串）
                if (clientIds.size() == 1 && clientIds.get(0).contains(Constants.SPLIT)) {
                    // 处理逗号分隔的字符串
                    commandIdList = Arrays.stream(clientIds.get(0).split(Constants.SPLIT))
                            .map(String::trim)
                            .filter(id -> !id.isEmpty())
                            .collect(Collectors.toList());
                } else {
                    commandIdList = clientIds;
                }
                log.info("使用配置文件中的客户端ID列表进行预热: {}", commandIdList);
            }

            log.info("开始自动装配AI客户端，客户端ID列表: {}", commandIdList);

            // 执行自动装配
            StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> armoryStrategyHandler =
                    defaultArmoryStrategyFactory.armoryStrategyHandler();

            String result = armoryStrategyHandler.apply(
                    ArmoryCommandEntity.builder()
                            .commandType(AiAgentEnumVO.AI_CLIENT.getCode())
                            .commandIdList(commandIdList)
                            .build(),
                    new DefaultArmoryStrategyFactory.DynamicContext());

            log.info("AI Agent 自动装配完成");
            
        } catch (Exception e) {
            log.error("AI Agent 自动装配失败", e);
        }
    }

}