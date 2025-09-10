package ai.agent;

import cn.bugstack.ai.infrastructure.dao.IAiAgentDao;
import cn.bugstack.ai.infrastructure.dao.po.AiAgent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.util.List;

/**
 * 配置化功能测试
 */
@SpringBootTest
@ActiveProfiles("test")
public class ConfigurableTest {

    @Resource
    private IAiAgentDao aiAgentDao;

    @Test
    public void testQueryAllAgentConfig() {
        List<AiAgent> agentList = aiAgentDao.queryAllAgentConfig();
        System.out.println("查询到的智能体配置数量: " + agentList.size());
        for (AiAgent agent : agentList) {
            System.out.println("智能体: " + agent.getAgentName() + ", 状态: " + agent.getStatus());
        }
    }

    @Test
    public void testQueryValidClientIds() {
        List<Long> clientIds = aiAgentDao.queryValidClientIds();
        System.out.println("查询到的有效客户端ID数量: " + clientIds.size());
        for (Long clientId : clientIds) {
            System.out.println("客户端ID: " + clientId);
        }
    }
}
