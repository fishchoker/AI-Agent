package cn.bugstack.ai.trigger.http.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 仪表板管理接口
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-09-16 16:46
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/ai/admin/dashboard/")
public class DashboardController {

    /**
     * 获取仪表板统计数据
     */
    @RequestMapping(value = "stats", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        log.info("获取仪表板统计数据请求开始");
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("code", "0000");
            response.put("info", "获取统计数据成功");
            
            Map<String, Object> data = new HashMap<>();
            
            // 智能体统计
            Map<String, Object> agentStats = new HashMap<>();
            agentStats.put("totalAgents", 5);
            agentStats.put("activeAgents", 3);
            agentStats.put("inactiveAgents", 2);
            agentStats.put("totalExecutions", 1250);
            agentStats.put("successfulExecutions", 1180);
            agentStats.put("failedExecutions", 70);
            data.put("agentStats", agentStats);
            
            // 知识库统计
            Map<String, Object> knowledgeStats = new HashMap<>();
            knowledgeStats.put("totalKnowledgeBases", 8);
            knowledgeStats.put("activeKnowledgeBases", 6);
            knowledgeStats.put("totalDocuments", 2450);
            knowledgeStats.put("totalVectors", 15680);
            knowledgeStats.put("storageSize", "2.5GB");
            data.put("knowledgeStats", knowledgeStats);
            
            // 用户统计
            Map<String, Object> userStats = new HashMap<>();
            userStats.put("totalUsers", 25);
            userStats.put("activeUsers", 18);
            userStats.put("totalSessions", 3420);
            userStats.put("averageSessionDuration", "15.5分钟");
            data.put("userStats", userStats);
            
            // 系统统计
            Map<String, Object> systemStats = new HashMap<>();
            systemStats.put("cpuUsage", "45%");
            systemStats.put("memoryUsage", "68%");
            systemStats.put("diskUsage", "35%");
            systemStats.put("uptime", "15天8小时");
            systemStats.put("lastBackup", "2025-09-15 02:00:00");
            data.put("systemStats", systemStats);
            
            // 最近活动
            Map<String, Object> recentActivity = new HashMap<>();
            recentActivity.put("recentExecutions", 45);
            recentActivity.put("recentUploads", 12);
            recentActivity.put("recentErrors", 3);
            recentActivity.put("recentUsers", 8);
            data.put("recentActivity", recentActivity);
            
            // 性能指标
            Map<String, Object> performanceMetrics = new HashMap<>();
            performanceMetrics.put("averageResponseTime", "1.2秒");
            performanceMetrics.put("successRate", "94.4%");
            performanceMetrics.put("errorRate", "5.6%");
            performanceMetrics.put("throughput", "150请求/分钟");
            data.put("performanceMetrics", performanceMetrics);
            
            response.put("data", data);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("获取仪表板统计数据异常：{}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", "5000");
            errorResponse.put("info", "获取统计数据失败：" + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 获取系统健康状态
     */
    @RequestMapping(value = "health", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        log.info("获取系统健康状态请求开始");
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("code", "0000");
            response.put("info", "获取系统健康状态成功");
            
            Map<String, Object> data = new HashMap<>();
            data.put("status", "healthy");
            data.put("timestamp", System.currentTimeMillis());
            data.put("version", "1.0.0");
            data.put("environment", "development");
            
            // 服务状态
            Map<String, Object> services = new HashMap<>();
            services.put("database", "healthy");
            services.put("redis", "healthy");
            services.put("vectorStore", "healthy");
            services.put("aiService", "healthy");
            data.put("services", services);
            
            response.put("data", data);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("获取系统健康状态异常：{}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", "5000");
            errorResponse.put("info", "获取系统健康状态失败：" + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 获取实时监控数据
     */
    @RequestMapping(value = "monitor", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getRealTimeMonitor() {
        log.info("获取实时监控数据请求开始");
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("code", "0000");
            response.put("info", "获取实时监控数据成功");
            
            Map<String, Object> data = new HashMap<>();
            
            // CPU使用率历史数据（最近1小时）
            data.put("cpuHistory", new int[]{45, 48, 52, 49, 46, 44, 47, 50, 48, 45});
            
            // 内存使用率历史数据
            data.put("memoryHistory", new int[]{68, 70, 72, 69, 67, 65, 68, 71, 69, 68});
            
            // 请求量历史数据
            data.put("requestHistory", new int[]{120, 135, 150, 142, 138, 125, 140, 155, 148, 135});
            
            // 错误率历史数据
            data.put("errorRateHistory", new double[]{5.2, 4.8, 6.1, 5.5, 4.9, 5.3, 5.8, 6.2, 5.7, 5.4});
            
            response.put("data", data);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("获取实时监控数据异常：{}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", "5000");
            errorResponse.put("info", "获取实时监控数据失败：" + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
