package com.java.birdme.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.java.birdme.bean.ReturnResp;
import com.java.birdme.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 将 Dify「对话」请求代理到官方 API，避免把 API Key 暴露在前端。
 * 官方文档：POST /v1/chat-messages（Chat 类应用）
 */
@RestController
public class DifyController {

    private static final Logger log = LoggerFactory.getLogger(DifyController.class);

    private static final String DEFAULT_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";

    @Value("${dify.api-base:https://api.dify.ai/v1}")
    private String apiBase;

    @Value("${dify.api-key:}")
    private String apiKey;

    @Value("${dify.user-agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36}")
    private String userAgent;

    @Value("${dify.http-proxy-host:}")
    private String httpProxyHost;

    @Value("${dify.http-proxy-port:0}")
    private int httpProxyPort;

    @PostMapping("/dify/chat-messages")
    public ResponseEntity<ReturnResp> chatMessages(@RequestBody Map<String, Object> body, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ReturnResp(401, "Unauthorized", null));
        }
        User user = (User) userObj;

        if (apiKey == null || apiKey.trim().isEmpty()) {
            return ResponseEntity.ok(ReturnResp.fail("Dify API key not configured (set dify.api-key in application.yml)"));
        }

        String query = body.get("query") == null ? "" : String.valueOf(body.get("query")).trim();
        if (query.isEmpty()) {
            return ResponseEntity.ok(ReturnResp.fail("query is required"));
        }

        String conversationId = body.get("conversationId") == null ? "" : String.valueOf(body.get("conversationId")).trim();
        Object inputsObj = body.get("inputs");
        if (!(inputsObj instanceof Map)) {
            inputsObj = new HashMap<String, Object>();
        }

        JSONObject payload = new JSONObject();
        payload.put("inputs", inputsObj);
        payload.put("query", query);
        payload.put("response_mode", "blocking");
        payload.put("user", "birdme-" + user.getId());
        if (!conversationId.isEmpty()) {
            payload.put("conversation_id", conversationId);
        }

        String base = apiBase.trim();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        String urlStr = base + "/chat-messages";

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = openConnection(url);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(120000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey.trim());
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
            conn.setRequestProperty("User-Agent", userAgent != null && !userAgent.trim().isEmpty()
                    ? userAgent.trim() : DEFAULT_USER_AGENT);
            conn.setDoOutput(true);

            byte[] bytes = payload.toJSONString().getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(bytes);
            }

            int status = conn.getResponseCode();
            String raw = readResponseBody(conn, status);
            if (status >= 400) {
                log.warn("Dify upstream HTTP {} — body length {}", status, raw != null ? raw.length() : 0);
                String msg = buildDifyErrorMessage(status, raw);
                return ResponseEntity.ok(new ReturnResp(status, msg, null));
            }

            JSONObject resp = JSON.parseObject(raw);
            String answer = resp.getString("answer");
            String newConversationId = resp.getString("conversation_id");

            Map<String, Object> data = new HashMap<>();
            data.put("answer", answer);
            data.put("conversationId", newConversationId);
            data.put("raw", resp);
            return ResponseEntity.ok(ReturnResp.success(data));
        } catch (Exception e) {
            return ResponseEntity.ok(ReturnResp.fail("Dify request failed: " + e.getMessage()));
        }
    }

    private HttpURLConnection openConnection(URL url) throws java.io.IOException {
        if (httpProxyHost != null && !httpProxyHost.trim().isEmpty() && httpProxyPort > 0) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpProxyHost.trim(), httpProxyPort));
            return (HttpURLConnection) url.openConnection(proxy);
        }
        return (HttpURLConnection) url.openConnection();
    }

    private static String readResponseBody(HttpURLConnection conn, int status) throws java.io.IOException {
        java.io.InputStream stream = null;
        if (status >= 400) {
            stream = conn.getErrorStream();
        }
        if (stream == null) {
            try {
                stream = conn.getInputStream();
            } catch (java.io.IOException ignored) {
                return "";
            }
        }
        if (stream == null) {
            return "";
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    private static String buildDifyErrorMessage(int status, String raw) {
        if (raw == null) {
            raw = "";
        }
        String lower = raw.toLowerCase();
        if (lower.contains("error code: 1010") || lower.contains("error 1010") || lower.contains("cf-error-details")) {
            return "Cloudflare Error 1010：服务端访问 Dify 地址被拒绝（常被判定为非浏览器客户端）。"
                    + " 可行方案：① 将 dify.api-base 改为自建/内网 Dify；② 配置 dify.http-proxy-host 与 dify.http-proxy-port"
                    + " 使用可访问 api.dify.ai 的出口代理；③ 若域名由你方接入 Cloudflare，可为 API 路径关闭 Browser Integrity Check 或放行服务器出口 IP。"
                    + " HTTP " + status + "。";
        }
        try {
            JSONObject o = JSON.parseObject(raw);
            if (o != null) {
                String message = o.getString("message");
                if (message != null && !message.isEmpty()) {
                    return "Dify API error (" + status + "): " + message;
                }
                String code = o.getString("code");
                if (code != null && !code.isEmpty()) {
                    return "Dify API error (" + status + "): " + code;
                }
            }
        } catch (Exception ignored) {
            // not JSON
        }
        String snippet = raw.length() > 480 ? raw.substring(0, 480) + "…" : raw;
        return "Dify API error (" + status + "): " + snippet;
    }
}
