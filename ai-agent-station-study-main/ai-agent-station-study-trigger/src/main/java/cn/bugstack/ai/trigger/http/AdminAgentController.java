package cn.bugstack.ai.trigger.http;

import cn.bugstack.ai.api.response.Response;
import cn.bugstack.ai.domain.agent.adapter.repository.IAgentRepository;
import cn.bugstack.ai.domain.agent.model.valobj.AiAgentVO;
import cn.bugstack.ai.types.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 管理后台 - AI智能体配置管理
 * 
 * @author xiaofuge bugstack.cn @小傅哥
 */
@Slf4j
@RestController
@RequestMapping("/ai/admin/agent")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class AdminAgentController {

    @Resource
    private IAgentRepository agentRepository;

    /**
     * 查询所有智能体配置列表（按渠道）
     * 
     * @param request 查询请求参数
     * @return 智能体配置列表
     */
    @PostMapping("/queryAllAgentConfigListByChannel")
    public Response<List<AiAgentVO>> queryAllAgentConfigListByChannel(@RequestBody QueryAgentRequest request) {
        log.info("🔔 查询智能体配置列表请求开始，请求信息：{}", request);
        
        try {
            // 参数校验
            if (request == null) {
                return Response.<List<AiAgentVO>>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info("请求参数不能为空")
                        .data(null)
                        .build();
            }
            
            // 查询智能体配置列表
            List<AiAgentVO> agentList;
            if (request.getChannel() != null && !request.getChannel().trim().isEmpty()) {
                // 按渠道查询
                agentList = agentRepository.queryAiAgentListByChannel(request.getChannel());
            } else {
                // 查询所有
                agentList = agentRepository.queryAiAgentList();
            }
            
            // 分页处理
            if (request.getPageNum() != null && request.getPageSize() != null) {
                int pageNum = Math.max(1, request.getPageNum());
                int pageSize = Math.max(1, Math.min(1000, request.getPageSize())); // 限制最大1000条
                
                int startIndex = (pageNum - 1) * pageSize;
                int endIndex = Math.min(startIndex + pageSize, agentList.size());
                
                if (startIndex < agentList.size()) {
                    agentList = agentList.subList(startIndex, endIndex);
                } else {
                    agentList = List.of(); // 空列表
                }
            }
            
            log.info("🔔 查询智能体配置列表成功，返回 {} 条记录", agentList.size());
            
            return Response.<List<AiAgentVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(agentList)
                    .build();
                    
        } catch (Exception e) {
            log.error("查询智能体配置列表异常：{}", e.getMessage(), e);
            return Response.<List<AiAgentVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info("查询失败：" + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * 查询请求参数
     */
    public static class QueryAgentRequest {
        private String channel;    // 渠道类型
        private Integer pageNum;   // 页码
        private Integer pageSize;  // 每页大小
        
        // Getters and Setters
        public String getChannel() {
            return channel;
        }
        
        public void setChannel(String channel) {
            this.channel = channel;
        }
        
        public Integer getPageNum() {
            return pageNum;
        }
        
        public void setPageNum(Integer pageNum) {
            this.pageNum = pageNum;
        }
        
        public Integer getPageSize() {
            return pageSize;
        }
        
        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
        }
        
        @Override
        public String toString() {
            return "QueryAgentRequest{" +
                    "channel='" + channel + '\'' +
                    ", pageNum=" + pageNum +
                    ", pageSize=" + pageSize +
                    '}';
        }
    }
}
