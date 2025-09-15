package cn.bugstack.ai.trigger.http;

import cn.bugstack.ai.api.IAiAgentService;
import cn.bugstack.ai.api.dto.AutoAgentRequestDTO;
import cn.bugstack.ai.domain.agent.model.entity.ExecuteCommandEntity;
import cn.bugstack.ai.domain.agent.service.IAgentDispatchService;
import cn.bugstack.ai.domain.agent.service.execute.IExecuteStrategy;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * AutoAgent 自动智能对话体
 *
 * @author xiaofuge bugstack.cn @小傅哥
 */
@Slf4j
@RestController
@RequestMapping("/ai/agent")  // 修改 base path 为 /ai/agent
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class AiAgentController implements IAiAgentService {

    @Resource(name = "autoAgentExecuteStrategy")
    private IExecuteStrategy autoAgentExecuteStrategy;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private IAgentDispatchService agentDispatchService;

    /**
     * SSE 流式接口
     */
    @PostMapping("/chat_stream")  // 修改方法路径为 chat_stream
    public ResponseBodyEmitter autoAgent(@RequestBody AutoAgentRequestDTO request, HttpServletResponse response) {
        log.info("🔔 AutoAgent流式执行请求开始，请求信息：{}", JSON.toJSONString(request));
        
        // 参数校验
        if (request == null) {
            log.error("请求参数不能为空");
            throw new IllegalArgumentException("请求参数不能为空");
        }
        
        // 设置 SSE 头
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");

        // 创建 ResponseBodyEmitter
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(Long.MAX_VALUE);

        try {
            // 构建执行命令实体，添加空值检查
            ExecuteCommandEntity executeCommandEntity = ExecuteCommandEntity.builder()
                    .aiAgentId(request.getAiAgentId() != null ? request.getAiAgentId() : "")
                    .message(request.getMessage() != null ? request.getMessage() : "")
                    .sessionId(request.getSessionId() != null ? request.getSessionId() : "")
                    .maxStep(request.getMaxStep() != null ? request.getMaxStep() : 5) // 默认最大步数
                    .build();

            // 异步执行
            threadPoolExecutor.submit(() -> {
                try {
                    agentDispatchService.dispatch(executeCommandEntity, emitter);
                    emitter.complete();  // 完成流
                } catch (Exception e) {
                    log.error("AutoAgent 执行异常：{}", e.getMessage(), e);
                    try {
                        emitter.send("执行异常：" + e.getMessage());
                        emitter.complete();
                    } catch (Exception ex) {
                        log.error("发送异常信息失败：{}", ex.getMessage(), ex);
                    }
                }
            });

        } catch (Exception e) {
            log.error("AutoAgent 请求处理异常：{}", e.getMessage(), e);
            try {
                emitter.send("请求处理异常：" + e.getMessage());
                emitter.complete();
            } catch (Exception ex) {
                log.error("发送异常信息失败：{}", ex.getMessage(), ex);
            }
        }

        return emitter;
    }


}