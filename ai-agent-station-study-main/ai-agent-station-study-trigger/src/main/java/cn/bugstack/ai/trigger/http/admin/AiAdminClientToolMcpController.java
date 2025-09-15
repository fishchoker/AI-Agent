package cn.bugstack.ai.trigger.http.admin;

import cn.bugstack.ai.infrastructure.dao.IAiClientToolMcpDao;
import cn.bugstack.ai.infrastructure.dao.po.AiClientToolMcp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MCP工具管理服务
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-06 16:46
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/ai/admin/client/tool/mcp/")
public class AiAdminClientToolMcpController {

    @Resource
    private IAiClientToolMcpDao aiClientToolMcpDao;

    /**
     * 获取MCP工具列表
     *
     * @param aiClientToolMcp 查询条件
     * @return 分页结果
     */
    @RequestMapping(value = "queryMcpList", method = RequestMethod.POST)
    public ResponseEntity<List<AiClientToolMcp>> queryMcpList(@RequestBody AiClientToolMcp aiClientToolMcp) {
        try {
            List<AiClientToolMcp> mcpList = aiClientToolMcpDao.queryAll();
            return ResponseEntity.ok(mcpList);
        } catch (Exception e) {
            log.error("查询MCP工具列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取MCP工具详情
     */
    @RequestMapping(value = "queryMcpDetail", method = RequestMethod.POST)
    public ResponseEntity<AiClientToolMcp> queryMcpDetail(@RequestBody AiClientToolMcp request) {
        try {
            AiClientToolMcp mcp = aiClientToolMcpDao.queryById(request.getId());
            return ResponseEntity.ok(mcp);
        } catch (Exception e) {
            log.error("查询MCP工具详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 新增MCP工具
     *
     * @param aiClientToolMcp MCP工具
     * @return 结果
     */
    @RequestMapping(value = "addMcp", method = RequestMethod.POST)
    public ResponseEntity<Boolean> addMcp(@RequestBody AiClientToolMcp aiClientToolMcp) {
        try {
            aiClientToolMcp.setCreateTime(LocalDateTime.now());
            aiClientToolMcp.setUpdateTime(LocalDateTime.now());
            int count = aiClientToolMcpDao.insert(aiClientToolMcp);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("新增MCP工具异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 更新MCP工具
     *
     * @param aiClientToolMcp MCP工具
     * @return 结果
     */
    @RequestMapping(value = "updateMcp", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateMcp(@RequestBody AiClientToolMcp aiClientToolMcp) {
        try {
            aiClientToolMcp.setUpdateTime(LocalDateTime.now());
            int count = aiClientToolMcpDao.updateById(aiClientToolMcp);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("更新MCP工具异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 删除MCP工具
     *
     * @param aiClientToolMcp MCP工具
     * @return 结果
     */
    @RequestMapping(value = "deleteMcp", method = RequestMethod.POST)
    public ResponseEntity<Boolean> deleteMcp(@RequestBody AiClientToolMcp aiClientToolMcp) {
        try {
            int count = aiClientToolMcpDao.deleteById(aiClientToolMcp.getId());
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("删除MCP工具异常", e);
            return ResponseEntity.status(500).build();
        }
    }

}
