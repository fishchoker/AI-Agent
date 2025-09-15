package cn.bugstack.ai.trigger.http.admin;

import cn.bugstack.ai.infrastructure.dao.IAiClientModelDao;
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
import java.util.List;

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

    

    /**
     * 查询客户端模型配置列表
     *
     * @param aiClientModel 查询条件
     * @return 模型配置列表
     */
    @RequestMapping(value = "queryClientModelConfigList", method = RequestMethod.POST)
    public ResponseEntity<List<AiClientModel>> queryClientModelConfigList(@RequestBody AiClientModel aiClientModel) {
        try {
            List<AiClientModel> modelList = aiClientModelDao.queryAll();
            return ResponseEntity.ok(modelList);
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
