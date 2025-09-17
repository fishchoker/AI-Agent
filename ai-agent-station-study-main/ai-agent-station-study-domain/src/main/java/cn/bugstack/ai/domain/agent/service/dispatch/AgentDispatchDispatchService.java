package cn.bugstack.ai.domain.agent.service.dispatch;

import cn.bugstack.ai.domain.agent.adapter.repository.IAgentRepository;
import cn.bugstack.ai.domain.agent.model.entity.ExecuteCommandEntity;
import cn.bugstack.ai.domain.agent.model.valobj.AiAgentVO;
import cn.bugstack.ai.domain.agent.service.IAgentDispatchService;
import cn.bugstack.ai.domain.agent.service.execute.IExecuteStrategy;
import cn.bugstack.ai.types.exception.BizException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Agent 服务接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/9/6 06:55
 */
@Slf4j
@Service
public class AgentDispatchDispatchService implements IAgentDispatchService {

    @Resource
    private Map<String, IExecuteStrategy> executeStrategyMap;

    @Resource
    private IAgentRepository repository;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void dispatch(ExecuteCommandEntity requestParameter, ResponseBodyEmitter emitter) throws Exception {
        AiAgentVO aiAgentVO = repository.queryAiAgentByAgentId(requestParameter.getAiAgentId());
        
        if (aiAgentVO == null) {
            String errorMsg = "未找到智能体配置，agentId: " + requestParameter.getAiAgentId();
            log.error(errorMsg);
            try {
                emitter.send("data: " + JSON.toJSONString(Map.of("error", errorMsg)) + "\n\n");
            } catch (Exception e) {
                log.error("发送错误信息失败：{}", e.getMessage(), e);
            } finally {
                try {
                    emitter.complete();
                } catch (Exception e) {
                    log.error("完成流式输出失败：{}", e.getMessage(), e);
                }
            }
            return;
        }

        String strategy = aiAgentVO.getStrategy();
        IExecuteStrategy executeStrategy = executeStrategyMap.get(strategy);
        if (null == executeStrategy) {
            String errorMsg = "不存在的执行策略类型 strategy:" + strategy;
            log.error(errorMsg);
            try {
                emitter.send("data: " + JSON.toJSONString(Map.of("error", errorMsg)) + "\n\n");
            } catch (Exception e) {
                log.error("发送错误信息失败：{}", e.getMessage(), e);
            } finally {
                try {
                    emitter.complete();
                } catch (Exception e) {
                    log.error("完成流式输出失败：{}", e.getMessage(), e);
                }
            }
            return;
        }

        // 3. 异步执行AutoAgent
        threadPoolExecutor.execute(() -> {
            try {
                log.info("开始执行智能体，agentId: {}, strategy: {}", requestParameter.getAiAgentId(), strategy);
                executeStrategy.execute(requestParameter, emitter);
            } catch (Exception e) {
                log.error("AutoAgent执行异常，agentId: {}, strategy: {}, 异常：{}", requestParameter.getAiAgentId(), strategy, e.getMessage(), e);
                try {
                    emitter.send("data: " + JSON.toJSONString(Map.of("error", "执行异常：" + e.getMessage())) + "\n\n");
                } catch (Exception ex) {
                    log.error("发送异常信息失败：{}", ex.getMessage(), ex);
                }
            } finally {
                try {
                    emitter.complete();
                } catch (Exception e) {
                    log.error("完成流式输出失败：{}", e.getMessage(), e);
                }
            }
        });

    }

}
