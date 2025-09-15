package cn.bugstack.ai.trigger.http.admin;

import cn.bugstack.ai.infrastructure.dao.IAiAgentDao;
import cn.bugstack.ai.infrastructure.dao.IAiAgentTaskScheduleDao;
import cn.bugstack.ai.infrastructure.dao.IAiClientDao;
import cn.bugstack.ai.infrastructure.dao.po.AiAgent;
import cn.bugstack.ai.infrastructure.dao.po.AiAgentTaskSchedule;
import cn.bugstack.ai.infrastructure.dao.po.AiClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * AI代理管理服务
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-06 16:46
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/ai/admin/agent/")
public class AiAdminAgentController {

    @Resource
    private IAiAgentDao aiAgentDao;

    @Resource
    private IAiAgentTaskScheduleDao aiAgentTaskScheduleDao;

    @Resource
    private IAiClientDao aiClientDao;

    /**
     * 分页查询AI代理列表
     *
     * @param aiAgent 查询条件
     * @return 分页结果
     */
    @RequestMapping(value = "queryAiAgentList", method = RequestMethod.POST)
    public ResponseEntity<List<AiAgent>> queryAiAgentList(@RequestBody AiAgent aiAgent) {
        try {
            List<AiAgent> agentList = aiAgentDao.queryAll();
            return ResponseEntity.ok(agentList);
        } catch (Exception e) {
            log.error("查询AI代理列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 根据渠道获取AI代理列表
     */
    @PostMapping("queryAllAgentConfigListByChannel")
    public ResponseEntity<List<AiAgent>> queryAllAgentConfigListByChannel(@RequestBody Map<String, Object> body) {
        try {
            // 打印请求体
            System.out.println("收到请求参数: " + body);

            // 获取 channel
            String channel = (String) body.get("channel");
            System.out.println("解析到 channel: " + channel);

            // 查询数据库
            List<AiAgent> agentList = aiAgentDao.queryByChannel(channel);

            // 打印查询结果
            System.out.println("查询结果: " + agentList);

            return ResponseEntity.ok(agentList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }



    /**
     * 获取AI代理详情 (POST方法)
     */
    @RequestMapping(value = "queryAiAgentDetail", method = RequestMethod.POST)
    public ResponseEntity<AiAgent> queryAiAgentDetail(@RequestBody AiAgent request) {
        try {
            AiAgent agent = aiAgentDao.queryById(request.getId());
            return ResponseEntity.ok(agent);
        } catch (Exception e) {
            log.error("查询AI代理详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取AI代理详情 (GET方法)
     */
    @RequestMapping(value = "queryAiAgentDetail", method = RequestMethod.GET)
    public ResponseEntity<AiAgent> queryAiAgentDetailGet(@RequestParam("id") Long id) {
        try {
            AiAgent agent = aiAgentDao.queryById(id);
            return ResponseEntity.ok(agent);
        } catch (Exception e) {
            log.error("查询AI代理详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 根据ID查询AI代理详情 (GET方法)
     */
    @RequestMapping(value = "queryAiAgentById", method = RequestMethod.GET)
    public ResponseEntity<AiAgent> queryAiAgentById(@RequestParam("id") Long id) {
        try {
            AiAgent agent = aiAgentDao.queryById(id);
            return ResponseEntity.ok(agent);
        } catch (Exception e) {
            log.error("根据ID查询AI代理详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 新增AI代理
     *
     * @param aiAgent AI代理
     * @return 结果
     */
    @RequestMapping(value = "addAiAgent", method = RequestMethod.POST)
    public ResponseEntity<Boolean> addAiAgent(@RequestBody AiAgent aiAgent) {
        try {
            // 基础校验与默认值
            if (aiAgent == null) {
                return ResponseEntity.badRequest().build();
            }
            if (aiAgent.getAgentId() == null || aiAgent.getAgentId().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (aiAgent.getAgentName() == null || aiAgent.getAgentName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (aiAgent.getChannel() == null || aiAgent.getChannel().trim().isEmpty()) {
                aiAgent.setChannel("agent");
            }
            if (aiAgent.getStrategy() == null || aiAgent.getStrategy().trim().isEmpty()) {
                aiAgent.setStrategy("flowAgentExecuteStrategy");
            }
            if (aiAgent.getStatus() == null) {
                aiAgent.setStatus(1);
            }
            aiAgent.setCreateTime(LocalDateTime.now());
            aiAgent.setUpdateTime(LocalDateTime.now());
            int count = aiAgentDao.insert(aiAgent);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("新增AI代理异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 更新AI代理
     *
     * @param aiAgent AI代理
     * @return 结果
     */
    @RequestMapping(value = "updateAiAgent", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateAiAgent(@RequestBody AiAgent aiAgent) {
        try {
            aiAgent.setUpdateTime(LocalDateTime.now());
            int count = aiAgentDao.updateById(aiAgent);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("更新AI代理异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 删除AI代理
     *
     * @param aiAgent AI代理
     * @return 结果
     */
    @RequestMapping(value = "deleteAiAgent", method = RequestMethod.POST)
    public ResponseEntity<Boolean> deleteAiAgent(@RequestBody AiAgent aiAgent) {
        try {
            int count = aiAgentDao.deleteById(aiAgent.getId());
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("删除AI代理异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 查询任务调度列表
     *
     * @param request 查询条件
     * @return 任务调度列表
     */
    @RequestMapping(value = "task/schedule/queryTaskScheduleList", method = RequestMethod.POST)
    public ResponseEntity<List<AiAgentTaskSchedule>> queryTaskScheduleList(@RequestBody Map<String, Object> request) {
        try {
            List<AiAgentTaskSchedule> taskScheduleList;
            
            // 如果传入了agentId，则根据agentId查询
            if (request.containsKey("agentId") && request.get("agentId") != null) {
                Long agentId = Long.valueOf(request.get("agentId").toString());
                taskScheduleList = aiAgentTaskScheduleDao.queryByAgentId(agentId);
            } else {
                // 否则查询所有任务调度
                taskScheduleList = aiAgentTaskScheduleDao.queryAll();
            }
            
            return ResponseEntity.ok(taskScheduleList);
        } catch (Exception e) {
            log.error("查询任务调度列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 根据智能体ID查询任务调度列表
     *
     * @param agentId 智能体ID
     * @return 任务调度列表
     */
    @RequestMapping(value = "task/schedule/queryTaskScheduleListByAgentId", method = RequestMethod.POST)
    public ResponseEntity<List<AiAgentTaskSchedule>> queryTaskScheduleListByAgentId(@RequestBody Map<String, Object> request) {
        try {
            Long agentId = Long.valueOf(request.get("agentId").toString());
            List<AiAgentTaskSchedule> taskScheduleList = aiAgentTaskScheduleDao.queryByAgentId(agentId);
            return ResponseEntity.ok(taskScheduleList);
        } catch (Exception e) {
            log.error("根据智能体ID查询任务调度列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 查询所有有效的任务调度
     *
     * @return 有效的任务调度列表
     */
    @RequestMapping(value = "task/schedule/queryEnabledTaskScheduleList", method = RequestMethod.POST)
    public ResponseEntity<List<AiAgentTaskSchedule>> queryEnabledTaskScheduleList() {
        try {
            List<AiAgentTaskSchedule> taskScheduleList = aiAgentTaskScheduleDao.queryEnabledTasks();
            return ResponseEntity.ok(taskScheduleList);
        } catch (Exception e) {
            log.error("查询有效任务调度列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 新增任务调度
     *
     * @param taskSchedule 任务调度配置
     * @return 结果
     */
    @RequestMapping(value = "task/schedule/addTaskSchedule", method = RequestMethod.POST)
    public ResponseEntity<Boolean> addTaskSchedule(@RequestBody AiAgentTaskSchedule taskSchedule) {
        try {
            taskSchedule.setCreateTime(LocalDateTime.now());
            taskSchedule.setUpdateTime(LocalDateTime.now());
            int count = aiAgentTaskScheduleDao.insert(taskSchedule);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("新增任务调度异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 更新任务调度
     *
     * @param taskSchedule 任务调度配置
     * @return 结果
     */
    @RequestMapping(value = "task/schedule/updateTaskSchedule", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateTaskSchedule(@RequestBody AiAgentTaskSchedule taskSchedule) {
        try {
            taskSchedule.setUpdateTime(LocalDateTime.now());
            int count = aiAgentTaskScheduleDao.updateById(taskSchedule);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("更新任务调度异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 删除任务调度
     *
     * @param request 包含任务调度ID的请求
     * @return 结果
     */
    @RequestMapping(value = "task/schedule/deleteTaskSchedule", method = RequestMethod.POST)
    public ResponseEntity<Boolean> deleteTaskSchedule(@RequestBody Map<String, Object> request) {
        try {
            Long id = Long.valueOf(request.get("id").toString());
            int count = aiAgentTaskScheduleDao.deleteById(id);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("删除任务调度异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 根据主键ID查询任务调度详情 (GET 方法)
     *
     * @param id 主键ID
     * @return 任务调度详情
     */
    @RequestMapping(value = "task/schedule/queryTaskScheduleById", method = RequestMethod.GET)
    public ResponseEntity<AiAgentTaskSchedule> queryTaskScheduleById(@RequestParam("id") Long id) {
        try {
            AiAgentTaskSchedule taskSchedule = aiAgentTaskScheduleDao.queryById(id);
            return ResponseEntity.ok(taskSchedule);
        } catch (Exception e) {
            log.error("根据主键ID查询任务调度详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 查询客户端列表
     *
     * @param request 查询条件
     * @return 客户端列表
     */
    @RequestMapping(value = "client/queryAgentClientList", method = RequestMethod.POST)
    public ResponseEntity<List<AiClient>> queryAgentClientList(@RequestBody Map<String, Object> request) {
        try {
            List<AiClient> clientList;
            
            // 如果传入了clientName，则根据clientName查询
            if (request.containsKey("clientName") && request.get("clientName") != null) {
                String clientName = request.get("clientName").toString();
                clientList = aiClientDao.queryByClientName(clientName);
            } else {
                // 否则查询所有客户端
                clientList = aiClientDao.queryAll();
            }
            
            return ResponseEntity.ok(clientList);
        } catch (Exception e) {
            log.error("查询客户端列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 根据客户端ID查询客户端详情
     *
     * @param request 包含客户端ID的请求
     * @return 客户端详情
     */
    @RequestMapping(value = "client/queryClientDetail", method = RequestMethod.POST)
    public ResponseEntity<AiClient> queryClientDetail(@RequestBody Map<String, Object> request) {
        try {
            String clientId = request.get("clientId").toString();
            AiClient client = aiClientDao.queryByClientId(clientId);
            return ResponseEntity.ok(client);
        } catch (Exception e) {
            log.error("查询客户端详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 根据主键ID查询客户端详情 (GET 方法)
     *
     * @param id 主键ID
     * @return 客户端详情
     */
    @RequestMapping(value = "client/queryAgentClientById", method = RequestMethod.GET)
    public ResponseEntity<AiClient> queryAgentClientById(@RequestParam("id") Long id) {
        try {
            AiClient client = aiClientDao.queryById(id);
            return ResponseEntity.ok(client);
        } catch (Exception e) {
            log.error("根据主键ID查询客户端详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 查询所有启用的客户端
     *
     * @return 启用的客户端列表
     */
    @RequestMapping(value = "client/queryEnabledClientList", method = RequestMethod.POST)
    public ResponseEntity<List<AiClient>> queryEnabledClientList() {
        try {
            List<AiClient> clientList = aiClientDao.queryEnabledClients();
            return ResponseEntity.ok(clientList);
        } catch (Exception e) {
            log.error("查询启用客户端列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 新增客户端
     *
     * @param client 客户端配置
     * @return 结果
     */
    @RequestMapping(value = "client/addClient", method = RequestMethod.POST)
    public ResponseEntity<Boolean> addClient(@RequestBody AiClient client) {
        try {
            client.setCreateTime(LocalDateTime.now());
            client.setUpdateTime(LocalDateTime.now());
            int count = aiClientDao.insert(client);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("新增客户端异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 更新客户端
     *
     * @param client 客户端配置
     * @return 结果
     */
    @RequestMapping(value = "client/updateClient", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateClient(@RequestBody AiClient client) {
        try {
            client.setUpdateTime(LocalDateTime.now());
            int count = aiClientDao.updateById(client);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("更新客户端异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 删除客户端
     *
     * @param request 包含客户端ID的请求
     * @return 结果
     */
    @RequestMapping(value = "client/deleteClient", method = RequestMethod.POST)
    public ResponseEntity<Boolean> deleteClient(@RequestBody Map<String, Object> request) {
        try {
            String clientId = request.get("clientId").toString();
            int count = aiClientDao.deleteByClientId(clientId);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("删除客户端异常", e);
            return ResponseEntity.status(500).build();
        }
    }
}
