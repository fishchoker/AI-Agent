package cn.bugstack.ai.trigger.http.admin;

import cn.bugstack.ai.infrastructure.dao.IAiClientConfigDao;
import cn.bugstack.ai.infrastructure.dao.IAiClientToolMcpDao;
import cn.bugstack.ai.infrastructure.dao.po.AiClientConfig;
import cn.bugstack.ai.infrastructure.dao.po.AiClientToolMcp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * AI客户端工具配置控制器
 * 
 * @author xiaofuge bugstack.cn @小傅哥
 */
@Slf4j
@RestController
@RequestMapping("/ai/admin/client/tool/config/")
public class AiAdminClientToolConfigController {

    @Resource
    private IAiClientToolMcpDao aiClientToolMcpDao;

    @Resource
    private IAiClientConfigDao aiClientConfigDao;

    /**
     * 查询客户端工具配置列表（补充 mcpName/description）
     */
    @RequestMapping(value = "queryClientToolConfigList", method = RequestMethod.POST)
    public ResponseEntity<List<Map<String, Object>>> queryClientToolConfigList(@RequestBody(required = false) Map<String, Object> request) {
        try {
            String filterClientId = request != null && request.get("clientId") != null ? String.valueOf(request.get("clientId")) : null;
            String filterToolId = request != null && request.get("toolId") != null ? String.valueOf(request.get("toolId")) : null;

            List<AiClientConfig> allConfigs = aiClientConfigDao.queryAll();
            // 预取所有 MCP 映射，避免多次查询
            List<AiClientToolMcp> mcps = aiClientToolMcpDao.queryAll();
            Map<String, AiClientToolMcp> mcpById = mcps.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(AiClientToolMcp::getMcpId, x -> x, (a, b) -> a));

            List<Map<String, Object>> rows = allConfigs.stream()
                    .filter(Objects::nonNull)
                    .filter(cfg -> "tool_mcp".equalsIgnoreCase(cfg.getTargetType()))
                    .filter(cfg -> filterClientId == null || filterClientId.equals(String.valueOf(cfg.getSourceId())))
                    .filter(cfg -> filterToolId == null || filterToolId.equals(String.valueOf(cfg.getTargetId())))
                    .map(cfg -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("configId", cfg.getId());
                        m.put("clientId", String.valueOf(cfg.getSourceId()));
                        m.put("targetType", "tool_mcp");
                        String mcpId = String.valueOf(cfg.getTargetId());
                        m.put("toolId", mcpId);
                        m.put("status", cfg.getStatus());
                        m.put("createTime", cfg.getCreateTime());
                        m.put("updateTime", cfg.getUpdateTime());
                        // 补充 mcp 信息（确保始终返回表格字段）
                        AiClientToolMcp mcp = mcpById.get(mcpId);
                        String mcpName = mcp != null && mcp.getMcpName() != null ? mcp.getMcpName() : "";
                        String transportType = mcp != null && mcp.getTransportType() != null ? mcp.getTransportType() : "";
                        // 前端所需字段
                        m.put("toolName", mcpName);
                        // 需求：description 对应数据表中的 transportType
                        m.put("description", transportType);
                        // 兼容：snake_case 字段
                        m.put("mcp_name", mcpName);
                        m.put("transport_type", transportType);
                        return m;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(rows);
        } catch (Exception e) {
            log.error("查询客户端工具配置列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    @RequestMapping(value = "queryClientToolConfigDetail", method = RequestMethod.POST)
    public ResponseEntity<AiClientToolMcp> queryClientToolConfigDetail(@RequestBody AiClientToolMcp request) {
        try {
            AiClientToolMcp config = aiClientToolMcpDao.queryById(request.getId());
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            log.error("查询客户端工具配置详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    @RequestMapping(value = "addClientToolConfig", method = RequestMethod.POST)
    public ResponseEntity<Boolean> addClientToolConfig(@RequestBody AiClientToolMcp aiClientToolMcp) {
        try {
            aiClientToolMcp.setCreateTime(LocalDateTime.now());
            aiClientToolMcp.setUpdateTime(LocalDateTime.now());
            int count = aiClientToolMcpDao.insert(aiClientToolMcp);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("新增客户端工具配置异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    @RequestMapping(value = "updateClientToolConfig", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateClientToolConfig(@RequestBody AiClientToolMcp aiClientToolMcp) {
        try {
            aiClientToolMcp.setUpdateTime(LocalDateTime.now());
            int count = aiClientToolMcpDao.updateById(aiClientToolMcp);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("更新客户端工具配置异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    @RequestMapping(value = "deleteClientToolConfig", method = RequestMethod.POST)
    public ResponseEntity<Boolean> deleteClientToolConfig(@RequestBody AiClientToolMcp aiClientToolMcp) {
        try {
            int count = aiClientToolMcpDao.deleteById(aiClientToolMcp.getId());
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("删除客户端工具配置异常", e);
            return ResponseEntity.status(500).build();
        }
    }
}
