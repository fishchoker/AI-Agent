package cn.bugstack.ai.trigger.http.admin;

import cn.bugstack.ai.infrastructure.dao.IAiClientAdvisorDao;
import cn.bugstack.ai.infrastructure.dao.po.AiClientAdvisor;
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

    /**
     * 查询客户端顾问配置列表
     *
     * @param aiClientAdvisor 查询条件
     * @return 顾问配置列表
     */
    @RequestMapping(value = "queryClientAdvisorConfigList", method = RequestMethod.POST)
    public ResponseEntity<List<AiClientAdvisor>> queryClientAdvisorConfigList(@RequestBody AiClientAdvisor aiClientAdvisor) {
        try {
            List<AiClientAdvisor> advisorList = aiClientAdvisorDao.queryAll();
            return ResponseEntity.ok(advisorList);
        } catch (Exception e) {
            log.error("查询客户端顾问配置列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

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
}
