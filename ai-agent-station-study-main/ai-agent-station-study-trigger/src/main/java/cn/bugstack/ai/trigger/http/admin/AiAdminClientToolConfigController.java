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
     * 查询客户端工具配置列表
     *
     * @param aiClientToolMcp 查询条件
     * @return 工具配置列表
     */
    @RequestMapping(value = "queryClientToolConfigList", method = RequestMethod.POST)
    public ResponseEntity<List<Map<String, Object>>> queryClientToolConfigList(@RequestBody(required = false) Map<String, Object> request) {
        try {
            String filterClientId = request != null && request.get("clientId") != null ? String.valueOf(request.get("clientId")) : null;
            String filterToolId = request != null && request.get("toolId") != null ? String.valueOf(request.get("toolId")) : null;
            // 可接收 pageNum/pageSize/searchType/searchValue，但当前不分页

            List<AiClientConfig> allConfigs = aiClientConfigDao.queryAll();

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
                        m.put("toolId", String.valueOf(cfg.getTargetId()));
                        m.put("status", cfg.getStatus());
                        m.put("createTime", cfg.getCreateTime());
                        m.put("updateTime", cfg.getUpdateTime());
                        return m;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(rows);
        } catch (Exception e) {
            log.error("查询客户端工具配置列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 查询客户端工具配置详情
     *
     * @param request 包含配置ID的请求
     * @return 工具配置详情
     */
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

    /**
     * 新增客户端工具配置
     *
     * @param aiClientToolMcp 工具配置
     * @return 结果
     */
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

    /**
     * 更新客户端工具配置
     *
     * @param aiClientToolMcp 工具配置
     * @return 结果
     */
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

    /**
     * 删除客户端工具配置
     *
     * @param aiClientToolMcp 工具配置
     * @return 结果
     */
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
