package cn.bugstack.ai.trigger.http;

import cn.bugstack.ai.infrastructure.dao.IAiClientRagOrderDao;
import cn.bugstack.ai.infrastructure.dao.po.AiClientRagOrder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/ai/knowledge/")
public class KnowledgeController {

    @Resource
    private IAiClientRagOrderDao aiClientRagOrderDao;

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public ResponseEntity<List<AiClientRagOrder>> listKnowledge() {
        try {
            // 返回启用的知识库列表
            List<AiClientRagOrder> ragOrderList = aiClientRagOrderDao.queryEnabledRagOrders();
            return ResponseEntity.ok(ragOrderList);
        } catch (Exception e) {
            log.error("获取知识库列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }
}


