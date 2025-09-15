package cn.bugstack.ai.trigger.http.admin;

import cn.bugstack.ai.infrastructure.dao.IAiClientModelDao;
import cn.bugstack.ai.infrastructure.dao.po.AiClientModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 客户端模型管理服务
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-06 16:46
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/ai/admin/client/model/")
public class AiAdminClientModelController {

    @Resource
    private IAiClientModelDao aiClientModelDao;

    /**
     * 查询客户端模型列表
     *
     * @param request 查询条件
     * @return 客户端模型列表
     */
    @RequestMapping(value = "queryClientModelList", method = RequestMethod.POST)
    public ResponseEntity<List<AiClientModel>> queryClientModelList(@RequestBody Map<String, Object> request) {
        try {
            List<AiClientModel> modelList;
            
            // 如果传入了modelType，则根据modelType查询
            if (request.containsKey("modelType") && request.get("modelType") != null) {
                String modelType = request.get("modelType").toString();
                modelList = aiClientModelDao.queryByModelType(modelType);
            } else if (request.containsKey("apiId") && request.get("apiId") != null) {
                // 如果传入了apiId，则根据apiId查询
                String apiId = request.get("apiId").toString();
                modelList = aiClientModelDao.queryByApiId(apiId);
            } else {
                // 否则查询所有模型
                modelList = aiClientModelDao.queryAll();
            }
            
            return ResponseEntity.ok(modelList);
        } catch (Exception e) {
            log.error("查询客户端模型列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 根据模型ID查询模型详情
     *
     * @param request 包含模型ID的请求
     * @return 模型详情
     */
    @RequestMapping(value = "queryModelDetail", method = RequestMethod.POST)
    public ResponseEntity<AiClientModel> queryModelDetail(@RequestBody Map<String, Object> request) {
        try {
            Long id = Long.valueOf(request.get("id").toString());
            AiClientModel model = aiClientModelDao.queryById(id);
            return ResponseEntity.ok(model);
        } catch (Exception e) {
            log.error("查询模型详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 查询所有启用的模型
     *
     * @return 启用的模型列表
     */
    @RequestMapping(value = "queryEnabledModelList", method = RequestMethod.POST)
    public ResponseEntity<List<AiClientModel>> queryEnabledModelList() {
        try {
            // 查询启用的模型
            List<AiClientModel> modelList = aiClientModelDao.queryEnabledModels();
            return ResponseEntity.ok(modelList);
        } catch (Exception e) {
            log.error("查询启用模型列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 新增模型
     *
     * @param model 模型配置
     * @return 结果
     */
    @RequestMapping(value = "addModel", method = RequestMethod.POST)
    public ResponseEntity<Boolean> addModel(@RequestBody AiClientModel model) {
        try {
            model.setCreateTime(LocalDateTime.now());
            model.setUpdateTime(LocalDateTime.now());
            int count = aiClientModelDao.insert(model);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("新增模型异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 更新模型
     *
     * @param model 模型配置
     * @return 结果
     */
    @RequestMapping(value = "updateModel", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateModel(@RequestBody AiClientModel model) {
        try {
            model.setUpdateTime(LocalDateTime.now());
            int count = aiClientModelDao.updateById(model);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("更新模型异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 删除模型
     *
     * @param request 包含模型ID的请求
     * @return 结果
     */
    @RequestMapping(value = "deleteModel", method = RequestMethod.POST)
    public ResponseEntity<Boolean> deleteModel(@RequestBody Map<String, Object> request) {
        try {
            Long id = Long.valueOf(request.get("id").toString());
            int count = aiClientModelDao.deleteById(id);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("删除模型异常", e);
            return ResponseEntity.status(500).build();
        }
    }
}
