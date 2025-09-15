package cn.bugstack.ai.trigger.http.admin;

import cn.bugstack.ai.infrastructure.dao.IAiClientConfigDao;
import cn.bugstack.ai.infrastructure.dao.IAiClientModelDao;
import cn.bugstack.ai.infrastructure.dao.po.AiClientConfig;
import cn.bugstack.ai.infrastructure.dao.po.AiClientModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * AI客户端模型配置控制器
 * 
 * @author xiaofuge bugstack.cn @小傅哥
 */
@Slf4j
@RestController
@RequestMapping("/ai/admin/client/model/config/")
public class AiAdminClientModelConfigController {

    @Resource
    private IAiClientModelDao aiClientModelDao;
    @Resource
    private IAiClientConfigDao aiClientConfigDao;
    

    /**
     * 查询客户端模型配置列表
     *
     * @param aiClientModel 查询条件
     * @return 模型配置列表
     */
    @RequestMapping(value = "queryClientModelConfigList", method = RequestMethod.POST)
    public ResponseEntity<List<Map<String, Object>>> queryClientModelConfigList(@RequestBody(required = false) Map<String, Object> request) {
        try {
            String filterClientId = request != null && request.get("clientId") != null ? String.valueOf(request.get("clientId")) : null;
            String filterModelId = request != null && request.get("modelId") != null ? String.valueOf(request.get("modelId")) : null;
            // 兼容前端分页与搜索参数（当前不做分页，仅透传过滤条件）
            // pageNum/pageSize/searchType/searchValue 可在此扩展

            List<AiClientConfig> allConfigs = aiClientConfigDao.queryAll();

            List<Map<String, Object>> rows = allConfigs.stream()
                    .filter(Objects::nonNull)
                    .filter(cfg -> "model".equalsIgnoreCase(cfg.getTargetType()))
                    .filter(cfg -> filterClientId == null || filterClientId.equals(String.valueOf(cfg.getSourceId())))
                    .filter(cfg -> filterModelId == null || filterModelId.equals(String.valueOf(cfg.getTargetId())))
                    .map(cfg -> {
                        Map<String, Object> m = new HashMap<>();
                        // 按前端字段命名约定输出
                        m.put("configId", cfg.getId());
                        m.put("clientId", String.valueOf(cfg.getSourceId()));
                        m.put("targetType", "model");
                        m.put("modelId", String.valueOf(cfg.getTargetId()));
                        m.put("extParam", cfg.getExtParam());
                        m.put("status", cfg.getStatus());
                        m.put("createTime", cfg.getCreateTime());
                        m.put("updateTime", cfg.getUpdateTime());
                        return m;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(rows);
        } catch (Exception e) {
            log.error("查询客户端模型配置列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 查询客户端模型配置详情
     *
     * @param request 包含配置ID的请求
     * @return 模型配置详情
     */
    @RequestMapping(value = "queryClientModelConfigDetail", method = RequestMethod.POST)
    public ResponseEntity<AiClientModel> queryClientModelConfigDetail(@RequestBody AiClientModel request) {
        try {
            AiClientModel model = aiClientModelDao.queryById(request.getId());
            return ResponseEntity.ok(model);
        } catch (Exception e) {
            log.error("查询客户端模型配置详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 根据主键ID查询模型配置详情 (GET 方法)
     *
     * @param id 主键ID
     * @return 模型配置详情
     */
    @RequestMapping(value = "queryClientModelConfigById", method = RequestMethod.GET)
    public ResponseEntity<AiClientModel> queryClientModelConfigById(@RequestParam("id") Long id) {
        try {
            AiClientModel model = aiClientModelDao.queryById(id);
            return ResponseEntity.ok(model);
        } catch (Exception e) {
            log.error("根据主键ID查询客户端模型配置详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 新增客户端模型配置
     *
     * @param aiClientModel 模型配置
     * @return 结果
     */
    @RequestMapping(value = "addClientModelConfig", method = RequestMethod.POST)
    public ResponseEntity<Boolean> addClientModelConfig(@RequestBody AiClientModel aiClientModel) {
        try {
            aiClientModel.setCreateTime(LocalDateTime.now());
            aiClientModel.setUpdateTime(LocalDateTime.now());
            int count = aiClientModelDao.insert(aiClientModel);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("新增客户端模型配置异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 更新客户端模型配置
     *
     * @param aiClientModel 模型配置
     * @return 结果
     */
    @RequestMapping(value = "updateClientModelConfig", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateClientModelConfig(@RequestBody AiClientModel aiClientModel) {
        try {
            aiClientModel.setUpdateTime(LocalDateTime.now());
            int count = aiClientModelDao.updateById(aiClientModel);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("更新客户端模型配置异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 删除客户端模型配置
     *
     * @param aiClientModel 模型配置
     * @return 结果
     */
    @RequestMapping(value = "deleteClientModelConfig", method = RequestMethod.POST)
    public ResponseEntity<Boolean> deleteClientModelConfig(@RequestBody AiClientModel aiClientModel) {
        try {
            int count = aiClientModelDao.deleteById(aiClientModel.getId());
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("删除客户端模型配置异常", e);
            return ResponseEntity.status(500).build();
        }
    }
}
