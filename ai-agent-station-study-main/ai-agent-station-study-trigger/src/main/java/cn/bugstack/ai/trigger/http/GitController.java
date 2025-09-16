package cn.bugstack.ai.trigger.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

import java.util.HashMap;
import java.util.Map;

/**
 * Git相关接口
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-09-16 16:46
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/ai/git/")
public class GitController {

    /**
     * 测试Git连接
     */
    @RequestMapping(value = "test-connection", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> testGitConnection(@RequestBody Map<String, Object> request) {
        // 从请求中提取参数
        String repoUrl = (String) request.get("repoUrl");
        String repoType = (String) request.get("repoType");
        String authType = (String) request.get("authType");
        String accessToken = (String) request.get("accessToken");
        String sshKey = (String) request.get("sshKey");
        
        log.info("测试Git连接请求开始，repoUrl：{}，repoType：{}，authType：{}", repoUrl, repoType, authType);
        
        Map<String, Object> response = new HashMap<>();
        try {
            // 提取仓库 owner 和 repo 名称
            String[] parts = repoUrl.replace("https://github.com/", "").split("/");
            if (parts.length < 2) {
                throw new IllegalArgumentException("无效的 GitHub 仓库 URL");
            }
            String owner = parts[0];
            String repo = parts[1].replaceAll(".git$", "");

            // 使用 GitHub API 查询仓库
            String apiUrl = String.format("https://api.github.com/repos/%s/%s", owner, repo);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            if (accessToken != null && !accessToken.isEmpty()) {
                // 设置 Token 认证
                headers.add("Authorization", "Bearer " + accessToken);
            }

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> apiResponse = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);

            if (apiResponse.getStatusCode() == HttpStatus.OK) {
                response.put("code", "0000");
                response.put("info", "GitHub 连接测试成功");
                Map<String, Object> data = new HashMap<>();
                data.put("connected", true);
                data.put("repoUrl", repoUrl);
                data.put("repoName", repo);
                data.put("owner", owner);
                data.put("repoType", repoType);
                data.put("authType", authType);
                response.put("data", data);
                return ResponseEntity.ok(response);
            } else {
                response.put("code", "5000");
                response.put("info", "GitHub 连接失败，状态码: " + apiResponse.getStatusCodeValue());
                response.put("data", null);
                return ResponseEntity.status(500).body(response);
            }

        } catch (Exception e) {
            response.put("code", "5000");
            response.put("info", "GitHub 连接测试异常：" + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 解析Git仓库
     */
    @RequestMapping(value = "parse-repo", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> parseGitRepository(@RequestBody Map<String, Object> request) {
        // 从请求中提取参数
        String repoUrl = (String) request.get("repoUrl");
        String repoType = (String) request.get("repoType");
        String authType = (String) request.get("authType");
        String accessToken = (String) request.get("accessToken");
        String sshKey = (String) request.get("sshKey");
        
        log.info("解析Git仓库请求开始，repoUrl：{}，repoType：{}，authType：{}", repoUrl, repoType, authType);
        
        try {
            // 提取仓库 owner 和 repo 名称
            String[] parts = repoUrl.replace("https://github.com/", "").split("/");
            if (parts.length < 2) {
                throw new IllegalArgumentException("无效的 GitHub 仓库 URL");
            }
            String owner = parts[0];
            String repo = parts[1].replaceAll(".git$", "");

            // 使用 GitHub API 获取仓库详细信息
            String apiUrl = String.format("https://api.github.com/repos/%s/%s", owner, repo);
            String contentsUrl = String.format("https://api.github.com/repos/%s/%s/contents", owner, repo);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            if (accessToken != null && !accessToken.isEmpty()) {
                headers.add("Authorization", "Bearer " + accessToken);
            }

            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // 获取仓库基本信息
            ResponseEntity<Map> repoResponse = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
            
            if (repoResponse.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> repoInfo = repoResponse.getBody();
                
                // 获取仓库内容（根目录文件列表）
                ResponseEntity<Map[]> contentsResponse = restTemplate.exchange(contentsUrl, HttpMethod.GET, entity, Map[].class);
                
                Map<String, Object> response = new HashMap<>();
                response.put("code", "0000");
                response.put("info", "仓库解析成功");
                
                Map<String, Object> data = new HashMap<>();
                data.put("repoId", repoInfo.get("id"));
                data.put("repoName", repoInfo.get("name"));
                data.put("owner", repoInfo.get("owner"));
                data.put("description", repoInfo.get("description"));
                data.put("language", repoInfo.get("language"));
                data.put("stars", repoInfo.get("stargazers_count"));
                data.put("forks", repoInfo.get("forks_count"));
                data.put("size", repoInfo.get("size"));
                data.put("defaultBranch", repoInfo.get("default_branch"));
                data.put("createdAt", repoInfo.get("created_at"));
                data.put("updatedAt", repoInfo.get("updated_at"));
                data.put("repoUrl", repoUrl);
                data.put("repoType", repoType);
                data.put("authType", authType);
                
                // 添加文件信息
                if (contentsResponse.getStatusCode() == HttpStatus.OK) {
                    Map[] contents = contentsResponse.getBody();
                    data.put("fileCount", contents != null ? contents.length : 0);
                    data.put("files", contents);
                } else {
                    data.put("fileCount", 0);
                    data.put("files", new Object[0]);
                }
                
                response.put("data", data);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", "5000");
                errorResponse.put("info", "获取仓库信息失败，状态码: " + repoResponse.getStatusCodeValue());
                errorResponse.put("data", null);
                return ResponseEntity.status(500).body(errorResponse);
            }

        } catch (Exception e) {
            log.error("解析Git仓库请求处理异常：{}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", "5000");
            errorResponse.put("info", "解析失败：" + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 解析Git仓库（备用接口）
     */
    @RequestMapping(value = "analyze-repository", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> analyzeGitRepository(@RequestBody Map<String, Object> request) {
        // 重定向到 parse-repo 接口
        return parseGitRepository(request);
    }
}
