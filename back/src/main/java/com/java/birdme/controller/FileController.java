package com.java.birdme.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.birdme.bean.ReturnResp;
import com.java.birdme.utils.Base64Util;
import com.java.birdme.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/file")
@CrossOrigin
public class FileController {
    @Value("${uploadPath}")
    private String UPLOAD_PATH;

    private static final String IDEN_API_KEY = "1nPjLzDo1mLTiF3r0HvrvC6f";
    private static final String IDEN_SECRET_KEY = "pOm625DOEOCnsPq0z8h3Eig2Np5dguPK";
    private static final String TRANS_API_KEY = "W2oWCPoUco3l2v0zLidduFhN";
    private static final String TRANS_SECRET_KEY = "MORswt8CTCzaFFR0L2z1LqmuaPnYMBMZ";
    private String accessToken;
    private long tokenExpireTime;

    /**
     * Get Baidu API access token
     */
    private synchronized String getAccessToken(String apiKey, String secretKey) {
        if (accessToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return accessToken;
        }

        try {
            String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + apiKey + "&client_secret=" + secretKey;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            if (responseCode != 200){
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonObject = JSON.parseObject(response.toString());
            accessToken = jsonObject.getString("access_token");
            int expiresIn = jsonObject.getIntValue("expires_in");
            tokenExpireTime = System.currentTimeMillis() + (expiresIn - 3600) * 1000; // Refresh 1 hour before expiration

            return accessToken;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Call Baidu Translate API to translate text to English
     */
    private String translateToEnglish(String text) {
        try {
            String url = "https://aip.baidubce.com/rpc/2.0/mt/texttrans/v1?access_token=" + getAccessToken(TRANS_API_KEY, TRANS_SECRET_KEY);
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            JSONObject requestBody = new JSONObject();
            requestBody.put("q", text);
            requestBody.put("from", "zh");
            requestBody.put("to", "en");

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = requestBody.toJSONString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonObject = JSON.parseObject(response.toString());
            if (jsonObject.containsKey("result")) {
                JSONObject result = jsonObject.getJSONObject("result");

                List<JSONObject> transResult = JSON.parseArray(result.getString("trans_result"), JSONObject.class);
                return transResult.get(0).getString("dst");
            }
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return text;
        }
    }

    @RequestMapping("/upload")
    public ReturnResp uploadFile(@RequestParam MultipartFile file) {
        if (file.isEmpty()) {
            return ReturnResp.fail("File cannot be empty");
        }
        try {
            byte[] bytes = file.getBytes();
            String id = UUID.randomUUID().toString().replaceAll("-", "");
            // Create upload directory if it doesn't exist
            File dir = new File(UPLOAD_PATH + File.separator + id);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String pathStr = UPLOAD_PATH + File.separator + id + File.separator + file.getOriginalFilename();
            Path path = Paths.get(pathStr);
            Files.write(path, bytes);

            Map<String, String> map = new HashMap<>();
            map.put("id", id);
            map.put("name", file.getOriginalFilename());
            return ReturnResp.success(map);
        } catch (IOException e) {
            e.printStackTrace();
            return ReturnResp.fail("File upload failed");
        }
    }

    @RequestMapping("/identification")
    public ReturnResp identification(@RequestParam MultipartFile file) {
        if (file.isEmpty()) {
            return ReturnResp.fail("File cannot be empty");
        }

        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/animal";
        try {
            // Read image file
            byte[] imgData = file.getBytes();
            // Encode image to Base64
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            String accessToken = getAccessToken(IDEN_API_KEY, IDEN_SECRET_KEY);

            String response = HttpUtil.post(url, accessToken, param);

            // Parse recognition result
            JSONObject jsonObject = JSON.parseObject(response);
            Map<String, Object> result = new HashMap<>();

            if (jsonObject.containsKey("result")) {
                JSONArray resultArray = jsonObject.getJSONArray("result");
                if (resultArray.size() > 0) {
                    JSONObject firstResult = resultArray.getJSONObject(0);
                    String birdName = firstResult.getString("name");
                    String birdDescription = firstResult.getString("description");

                    // Translate to English
                    String englishName = translateToEnglish(birdName);
                    String englishDescription = translateToEnglish(birdDescription);

                    result.put("name", englishName);
                    result.put("description", englishDescription);
                    result.put("score", firstResult.getDouble("score"));
                }
            }

            return ReturnResp.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResp.fail("Identification failed: " + e.getMessage());
        }
    }

    /**
     * Download file
     */
    @RequestMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam("id") String id, HttpServletRequest request, HttpServletResponse response) {
        try {
            String dir = UPLOAD_PATH + File.separator + id;
            File file = Objects.requireNonNull(new File(dir).listFiles())[0];

            // Create InputStreamResource object
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            // Set HTTP response headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            headers.add("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(404).build();
        }
    }
}
