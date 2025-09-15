package cn.bugstack.ai.trigger.http.admin;

import cn.bugstack.ai.infrastructure.dao.IAiClientRagOrderDao;
import cn.bugstack.ai.infrastructure.dao.po.AiClientRagOrder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * RAG订单管理服务
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-05-06 16:46
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/ai/admin/rag/")
public class AiAdminRagOrderController {

    @Resource
    private IAiClientRagOrderDao aiClientRagOrderDao;

    /**
     * 分页查询RAG订单列表
     *
     * @param aiRagOrder 查询条件
     * @return 分页结果
     */
    @RequestMapping(value = "queryRagOrderList", method = RequestMethod.POST)
    public ResponseEntity<List<AiClientRagOrder>> queryRagOrderList(@RequestBody AiClientRagOrder aiClientRagOrder) {
        try {
            List<AiClientRagOrder> ragOrderList = aiClientRagOrderDao.queryAll();
            return ResponseEntity.ok(ragOrderList);
        } catch (Exception e) {
            log.error("查询RAG订单列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取所有有效知识库列表
     */
    @RequestMapping(value = "queryAllValidRagOrder", method = RequestMethod.POST)
    public ResponseEntity<List<AiClientRagOrder>> queryAllValidRagOrder() {
        try {
            List<AiClientRagOrder> ragOrderList = aiClientRagOrderDao.queryEnabledRagOrders();
            return ResponseEntity.ok(ragOrderList);
        } catch (Exception e) {
            log.error("查询RAG订单列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取知识库详情
     */
    @RequestMapping(value = "queryRagOrderDetail", method = RequestMethod.POST)
    public ResponseEntity<AiClientRagOrder> queryRagOrderDetail(@RequestBody AiClientRagOrder request) {
        try {
            AiClientRagOrder ragOrder = aiClientRagOrderDao.queryById(request.getId());
            return ResponseEntity.ok(ragOrder);
        } catch (Exception e) {
            log.error("查询RAG订单详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 新增RAG订单
     *
     * @param aiRagOrder RAG订单
     * @return 结果
     */
    @RequestMapping(value = "addRagOrder", method = RequestMethod.POST)
    public ResponseEntity<Boolean> addRagOrder(@RequestBody AiClientRagOrder aiClientRagOrder) {
        try {
            aiClientRagOrder.setCreateTime(LocalDateTime.now());
            aiClientRagOrder.setUpdateTime(LocalDateTime.now());
            int count = aiClientRagOrderDao.insert(aiClientRagOrder);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("新增RAG订单异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 更新RAG订单
     *
     * @param aiRagOrder RAG订单
     * @return 结果
     */
    @RequestMapping(value = "updateRagOrder", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateRagOrder(@RequestBody AiClientRagOrder aiClientRagOrder) {
        try {
            aiClientRagOrder.setUpdateTime(LocalDateTime.now());
            int count = aiClientRagOrderDao.updateById(aiClientRagOrder);
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("更新RAG订单异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 删除RAG订单
     *
     * @param aiRagOrder RAG订单
     * @return 结果
     */
    @RequestMapping(value = "deleteRagOrder", method = RequestMethod.POST)
    public ResponseEntity<Boolean> deleteRagOrder(@RequestBody AiClientRagOrder aiClientRagOrder) {
        try {
            int count = aiClientRagOrderDao.deleteById(aiClientRagOrder.getId());
            return ResponseEntity.ok(count > 0);
        } catch (Exception e) {
            log.error("删除RAG订单异常", e);
            return ResponseEntity.status(500).build();
        }
    }
}
