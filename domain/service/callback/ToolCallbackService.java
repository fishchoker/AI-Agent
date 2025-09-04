package cn.bugstack.ai.domain.agent.service.callback;

import cn.bugstack.ai.domain.agent.model.valobj.ToolCallbackConfigVO;
import cn.bugstack.ai.domain.agent.adapter.repository.IAgentRepository;
import io.modelcontextprotocol.client.McpSyncClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 工具回调服务
 * 根据配置创建工具回调提供者
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@Slf4j
@Service
public class ToolCallbackService {
    
    @Resource
    private IAgentRepository repository;

    /**
     * 根据配置创建工具回调提供者
     * 替代硬编码的工具回调创建逻辑
     */
    public SyncMcpToolCallbackProvider createToolCallbackProvider(ToolCallbackConfigVO config, 
                                                                List<McpSyncClient> mcpClients) {
        try {
            // 根据配置类型创建不同的提供者
            switch (config.getCallbackType()) {
                case "mcp":
                    return new SyncMcpToolCallbackProvider(mcpClients.toArray(new McpSyncClient[0]));
                case "function_call":
                    // 可以扩展支持function call类型
                    log.warn("Function call类型暂未实现");
                    return null;
                default:
                    log.warn("不支持的工具栏调类型: {}", config.getCallbackType());
                    return null;
            }
        } catch (Exception e) {
            log.error("创建工具回调提供者失败，类型: {}", config.getCallbackType(), e);
            return null;
        }
    }
    
    /**
     * 根据回调类型创建工具回调提供者
     */
    public SyncMcpToolCallbackProvider createToolCallbackProviderByType(String callbackType, 
                                                                       List<McpSyncClient> mcpClients) {
        // 从数据库查询工具回调配置
        ToolCallbackConfigVO config = repository.queryToolCallbackConfigByType(callbackType);
        if (config != null) {
            return createToolCallbackProvider(config, mcpClients);
        }
        
        // 如果数据库中没有配置，使用默认实现
        if ("mcp".equals(callbackType)) {
            return new SyncMcpToolCallbackProvider(mcpClients.toArray(new McpSyncClient[0]));
        }
        
        log.warn("未找到工具回调配置: {}", callbackType);
        return null;
    }
}
