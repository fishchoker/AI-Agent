package cn.bugstack.ai.trigger.http.admin;

import cn.bugstack.ai.infrastructure.dao.IAiClientSystemPromptDao;
import cn.bugstack.ai.infrastructure.dao.po.AiClientSystemPrompt;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户端系统提示词管理服务
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-06 16:46
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/ai/admin/client/system/prompt/")
public class AiAdminClientSystemPromptController {

    @Resource
    private IAiClientSystemPromptDao aiClientSystemPromptDao;

    /**
     * 获取所有系统提示词
     */
    @RequestMapping(value = "queryAllSystemPromptConfig", method = RequestMethod.POST)
    public ResponseEntity<List<AiClientSystemPrompt>> queryAllSystemPromptConfig() {
        try {
            List<AiClientSystemPrompt> promptList = aiClientSystemPromptDao.queryAll();
            return ResponseEntity.ok(promptList);
        } catch (Exception e) {
            log.error("查询系统提示词列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取系统提示词详情
     */
    @RequestMapping(value = "querySystemPromptConfigDetail", method = RequestMethod.POST)
    public ResponseEntity<AiClientSystemPrompt> querySystemPromptConfigDetail(@RequestBody AiClientSystemPrompt request) {
        try {
            AiClientSystemPrompt prompt = aiClientSystemPromptDao.queryById(request.getId());
            return ResponseEntity.ok(prompt);
        } catch (Exception e) {
            log.error("查询系统提示词详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 根据主键ID查询系统提示词详情 (GET 方法)
     *
     * @param id 主键ID
     * @return 系统提示词详情
     */
    @RequestMapping(value = "querySystemPromptById", method = RequestMethod.GET)
    public ResponseEntity<AiClientSystemPrompt> querySystemPromptById(@RequestParam("id") Long id) {
        try {
            AiClientSystemPrompt prompt = aiClientSystemPromptDao.queryById(id);
            return ResponseEntity.ok(prompt);
        } catch (Exception e) {
            log.error("根据主键ID查询系统提示词详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 新增系统提示词
     *
     * @param aiClientSystemPromptConfig 系统提示词
     * @return 结果
     */
    @RequestMapping(value = "addSystemPromptConfig", method = RequestMethod.POST)
    public ResponseEntity<Boolean> addSystemPromptConfig(@RequestBody AiClientSystemPrompt aiClientSystemPrompt) {
        try {
            aiClientSystemPrompt.setCreateTime(LocalDateTime.now());
            aiClientSystemPrompt.setUpdateTime(LocalDateTime.now());
            aiClientSystemPromptDao.insert(aiClientSystemPrompt);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            log.error("新增系统提示词异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 更新系统提示词
     *
     * @param aiClientSystemPromptConfig 系统提示词
     * @return 结果
     */
    @RequestMapping(value = {"updateSystemPromptConfig", "updateSystemPrompt"}, method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateSystemPromptConfig(@RequestBody AiClientSystemPrompt aiClientSystemPrompt) {
        try {
            aiClientSystemPrompt.setUpdateTime(LocalDateTime.now());
            int count = aiClientSystemPromptDao.updateById(aiClientSystemPrompt);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("更新系统提示词异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 删除系统提示词
     *
     * @param aiClientSystemPromptConfig 系统提示词
     * @return 结果
     */
    @RequestMapping(value = "deleteSystemPromptConfig", method = RequestMethod.POST)
    public ResponseEntity<Boolean> deleteSystemPromptConfig(@RequestBody AiClientSystemPrompt aiClientSystemPrompt) {
        try {
            int count = aiClientSystemPromptDao.deleteById(aiClientSystemPrompt.getId());
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("删除系统提示词异常", e);
            return ResponseEntity.status(500).build();
        }
    }
}
