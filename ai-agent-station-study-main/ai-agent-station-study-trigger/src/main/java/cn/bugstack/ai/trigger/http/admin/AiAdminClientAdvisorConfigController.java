package cn.bugstack.ai.trigger.http.admin;

import cn.bugstack.ai.infrastructure.dao.IAiClientAdvisorDao;
import cn.bugstack.ai.infrastructure.dao.IAiClientConfigDao;
import cn.bugstack.ai.infrastructure.dao.po.AiClientAdvisor;
import cn.bugstack.ai.infrastructure.dao.po.AiClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * AI客户端顾问配置控制器
 * 
 * @author xiaofuge bugstack.cn @小傅哥
 */
@Slf4j
@RestController
@RequestMapping("/ai/admin/client/advisor/config/")
public class AiAdminClientAdvisorConfigController {

    @Resource
    private IAiClientAdvisorDao aiClientAdvisorDao;

    @Resource
    private IAiClientConfigDao aiClientConfigDao;



    /**
     * 查询客户端顾问配置详情
     *
     * @param request 包含配置ID的请求
     * @return 顾问配置详情
     */
    @RequestMapping(value = "queryClientAdvisorConfigDetail", method = RequestMethod.POST)
    public ResponseEntity<AiClientAdvisor> queryClientAdvisorConfigDetail(@RequestBody AiClientAdvisor request) {
        try {
            AiClientAdvisor advisor = aiClientAdvisorDao.queryById(request.getId());
            return ResponseEntity.ok(advisor);
        } catch (Exception e) {
            log.error("查询客户端顾问配置详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 根据主键ID查询顾问配置详情 (GET 方法)
     *
     * @param id 主键ID
     * @return 顾问配置详情
     */
    @RequestMapping(value = "queryClientAdvisorConfigById", method = RequestMethod.GET)
    public ResponseEntity<AiClientAdvisor> queryClientAdvisorConfigById(@RequestParam("id") Long id) {
        try {
            log.info("查询顾问配置详情，请求ID: {}", id);
            AiClientAdvisor advisor = aiClientAdvisorDao.queryById(id);
            if (advisor == null) {
                log.warn("未找到顾问配置，ID: {}", id);
                return ResponseEntity.ok().body(null);
            }
            return ResponseEntity.ok(advisor);
        } catch (Exception e) {
            log.error("根据主键ID查询客户端顾问配置详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 新增客户端顾问配置
     *
     * @param aiClientAdvisor 顾问配置
     * @return 结果
     */
    @RequestMapping(value = "addClientAdvisorConfig", method = RequestMethod.POST)
    public ResponseEntity<Boolean> addClientAdvisorConfig(@RequestBody AiClientAdvisor aiClientAdvisor) {
        try {
            aiClientAdvisor.setCreateTime(LocalDateTime.now());
            aiClientAdvisor.setUpdateTime(LocalDateTime.now());
            int count = aiClientAdvisorDao.insert(aiClientAdvisor);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("新增客户端顾问配置异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 更新客户端顾问配置
     *
     * @param aiClientAdvisor 顾问配置
     * @return 结果
     */
    @RequestMapping(value = "updateClientAdvisorConfig", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateClientAdvisorConfig(@RequestBody AiClientAdvisor aiClientAdvisor) {
        try {
            aiClientAdvisor.setUpdateTime(LocalDateTime.now());
            int count = aiClientAdvisorDao.updateById(aiClientAdvisor);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("更新客户端顾问配置异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 删除客户端顾问配置
     *
     * @param aiClientAdvisor 顾问配置
     * @return 结果
     */
    @RequestMapping(value = "deleteClientAdvisorConfig", method = RequestMethod.POST)
    public ResponseEntity<Boolean> deleteClientAdvisorConfig(@RequestBody AiClientAdvisor aiClientAdvisor) {
        try {
            int count = aiClientAdvisorDao.deleteById(aiClientAdvisor.getId());
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("删除客户端顾问配置异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 查询 ai_client_config 中 target_type='advisor' 的绑定关系列表
     * 返回: [{clientId, advisorId, status, createTime, updateTime}, ...]
     */
    @RequestMapping(value = "queryClientAdvisorConfigList", method = RequestMethod.POST)
    public ResponseEntity<List<Map<String, Object>>> queryClientAdvisorConfigList(@RequestBody(required = false) Map<String, Object> request) {
        try {
            String filterClientId = request != null && request.get("clientId") != null ? String.valueOf(request.get("clientId")) : null;
            String filterAdvisorId = request != null && request.get("advisorId") != null ? String.valueOf(request.get("advisorId")) : null;

            List<AiClientConfig> allConfigs = aiClientConfigDao.queryAll();

            List<Map<String, Object>> bindings = allConfigs.stream()
                    .filter(Objects::nonNull)
                    .filter(cfg -> "advisor".equalsIgnoreCase(cfg.getTargetType()))
                    .filter(cfg -> filterClientId == null || filterClientId.equals(String.valueOf(cfg.getSourceId())))
                    .filter(cfg -> filterAdvisorId == null || filterAdvisorId.equals(String.valueOf(cfg.getTargetId())))
                    .map(cfg -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("clientId", String.valueOf(cfg.getSourceId()));
                        m.put("advisorId", String.valueOf(cfg.getTargetId()));
                        m.put("status", cfg.getStatus());
                        m.put("createTime", cfg.getCreateTime());
                        m.put("updateTime", cfg.getUpdateTime());
                        return m;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(bindings);
        } catch (Exception e) {
            log.error("查询客户端顾问配置列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }
}
