package cn.bugstack.ai.trigger.http.admin;

import cn.bugstack.ai.infrastructure.dao.IAiClientAdvisorDao;
import cn.bugstack.ai.infrastructure.dao.po.AiClientAdvisor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 客户端顾问管理服务
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-06 16:46
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/ai/admin/client/advisor/")
public class AiAdminClientAdvisorController {

    @Resource
    private IAiClientAdvisorDao aiClientAdvisorDao;

    /**
     * 查询客户端顾问列表
     *
     * @param request 查询条件
     * @return 客户端顾问列表
     */
    @RequestMapping(value = "queryClientAdvisorList", method = RequestMethod.POST)
    public ResponseEntity<List<AiClientAdvisor>> queryClientAdvisorList(@RequestBody Map<String, Object> request) {
        try {
            List<AiClientAdvisor> advisorList;
            
            // 如果传入了advisorType，则根据advisorType查询
            if (request.containsKey("advisorType") && request.get("advisorType") != null) {
                String advisorType = request.get("advisorType").toString();
                advisorList = aiClientAdvisorDao.queryByAdvisorType(advisorType);
            } else if (request.containsKey("status") && request.get("status") != null) {
                // 如果传入了status，则根据status查询
                Integer status = Integer.valueOf(request.get("status").toString());
                advisorList = aiClientAdvisorDao.queryByStatus(status);
            } else {
                // 否则查询所有顾问
                advisorList = aiClientAdvisorDao.queryAll();
            }
            
            return ResponseEntity.ok(advisorList);
        } catch (Exception e) {
            log.error("查询客户端顾问列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 根据顾问ID查询顾问详情
     *
     * @param request 包含顾问ID的请求
     * @return 顾问详情
     */
    @RequestMapping(value = "queryAdvisorDetail", method = RequestMethod.POST)
    public ResponseEntity<AiClientAdvisor> queryAdvisorDetail(@RequestBody Map<String, Object> request) {
        try {
            Long id = Long.valueOf(request.get("id").toString());
            AiClientAdvisor advisor = aiClientAdvisorDao.queryById(id);
            return ResponseEntity.ok(advisor);
        } catch (Exception e) {
            log.error("查询顾问详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 根据主键ID查询顾问详情 (GET 方法)
     *
     * @param id 主键ID
     * @return 顾问详情
     */
    @RequestMapping(value = "queryClientAdvisorById", method = RequestMethod.GET)
    public ResponseEntity<AiClientAdvisor> queryClientAdvisorById(@RequestParam("id") Long id) {
        try {
            log.info("查询顾问详情，请求ID: {}", id);
            AiClientAdvisor advisor = aiClientAdvisorDao.queryById(id);
            if (advisor == null) {
                log.warn("未找到顾问信息，ID: {}", id);
                return ResponseEntity.ok().body(null);
            }
            return ResponseEntity.ok(advisor);
        } catch (Exception e) {
            log.error("根据主键ID查询顾问详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 查询所有启用的顾问
     *
     * @return 启用的顾问列表
     */
    @RequestMapping(value = "queryEnabledAdvisorList", method = RequestMethod.POST)
    public ResponseEntity<List<AiClientAdvisor>> queryEnabledAdvisorList() {
        try {
            // 查询状态为1（启用）的顾问
            List<AiClientAdvisor> advisorList = aiClientAdvisorDao.queryByStatus(1);
            return ResponseEntity.ok(advisorList);
        } catch (Exception e) {
            log.error("查询启用顾问列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 新增顾问
     *
     * @param advisor 顾问配置
     * @return 结果
     */
    @RequestMapping(value = "addAdvisor", method = RequestMethod.POST)
    public ResponseEntity<Boolean> addAdvisor(@RequestBody AiClientAdvisor advisor) {
        try {
            advisor.setCreateTime(LocalDateTime.now());
            advisor.setUpdateTime(LocalDateTime.now());
            int count = aiClientAdvisorDao.insert(advisor);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("新增顾问异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 更新顾问
     *
     * @param advisor 顾问配置
     * @return 结果
     */
    @RequestMapping(value = {"updateClientAdvisor", "advisor/updateClientAdvisor"}, method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateAdvisor(@RequestBody AiClientAdvisor advisor) {
        try {
            advisor.setUpdateTime(LocalDateTime.now());
            int count = aiClientAdvisorDao.updateById(advisor);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("更新顾问异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 删除顾问
     *
     * @param request 包含顾问ID的请求
     * @return 结果
     */
    @RequestMapping(value = "deleteAdvisor", method = RequestMethod.POST)
    public ResponseEntity<Boolean> deleteAdvisor(@RequestBody Map<String, Object> request) {
        try {
            Long id = Long.valueOf(request.get("id").toString());
            int count = aiClientAdvisorDao.deleteById(id);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("删除顾问异常", e);
            return ResponseEntity.status(500).build();
        }
    }
}
