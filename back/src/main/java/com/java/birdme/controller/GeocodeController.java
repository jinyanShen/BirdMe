package com.java.birdme.controller;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.java.birdme.bean.ReturnResp;
import com.java.birdme.bean.RescueStation;
import com.java.birdme.bean.User;
import com.java.birdme.dao.RescueStationMapper;
import com.java.birdme.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;

@RestController
public class GeocodeController {
    private static final String WEB_SERVICE_KEY = "4adb7f7330870496e1ae8b973e73796c"; // Web服务key
    private static final String SECRET = "c7adcba4c16b55f2ea608ad4104d68a2"; // 安全密钥
    private static final String QWEN_API_KEY = "sk-4afbdc639dd24b9fab8791bc4aa11e8b"; // 安全密钥

    @Autowired
    private RescueStationMapper rescueStationMapper;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/geocode")
    public ReturnResp geocode(@RequestBody Map<String, String> request) {
        try {
            String address = request.get("address");
            if (address == null || address.isEmpty()) {
                return new ReturnResp(400, "Address is required", null);
            }

            // 构建请求参数
            Map<String, String> params = new TreeMap<>();
            params.put("address", address);
            params.put("key", WEB_SERVICE_KEY);
            params.put("output", "json");
            params.put("lang", "en"); // Return English address

            // 生成签名
            String signature = generateSignature(params, SECRET);
            params.put("sig", signature);

            // 构建请求URL
            StringBuilder urlBuilder = new StringBuilder("https://restapi.amap.com/v3/geocode/geo?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
            }
            String urlStr = urlBuilder.toString().substring(0, urlBuilder.length() - 1);

            // 发送请求
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // 读取响应
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            // 解析响应
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(response.toString());
            if ("1".equals(jsonObject.getString("status"))) {
                com.alibaba.fastjson.JSONArray geocodes = jsonObject.getJSONArray("geocodes");
                if (geocodes.size() > 0) {
                    List<Map<String, Object>> results = new ArrayList<>();
                    for (int i = 0; i < geocodes.size(); i++) {
                        com.alibaba.fastjson.JSONObject geocode = geocodes.getJSONObject(i);
                        String location = geocode.getString("location");
                        String[] coords = location.split(",");
                        Map<String, Object> result = new HashMap<>();
                        result.put("address", translate(geocode.getString("formatted_address")));
                        result.put("longitude", Double.parseDouble(coords[0]));
                        result.put("latitude", Double.parseDouble(coords[1]));

                        // Try to get POI details
                        Map<String, Object> poiDetails = getPoiDetails(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), geocode.getString("formatted_address"));
                        if (poiDetails != null) {
                            result.putAll(poiDetails);
                        }

                        results.add(result);
                    }
                    return new ReturnResp(200, "Success", results);
                }
            }

            return new ReturnResp(404, "Location not found", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnResp(500, "Server error", null);
        }
    }

    private Map<String, Object> getPoiDetails(double longitude, double latitude, String keyword) throws Exception {
        // 构建POI搜索请求参数
        Map<String, String> params = new TreeMap<>();
        params.put("location", longitude + "," + latitude);
        params.put("keywords", keyword);
        params.put("radius", "1000");
        params.put("extensions", "base");
        params.put("output", "json");
        params.put("key", WEB_SERVICE_KEY);
        params.put("lang", "en"); // Return English POI information

        // 生成签名
        String signature = generateSignature(params, SECRET);
        params.put("sig", signature);

        // 构建请求URL
        StringBuilder urlBuilder = new StringBuilder("https://restapi.amap.com/v3/place/around?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.append(entry.getKey()).append("=")
                    .append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
        }
        String urlStr = urlBuilder.toString().substring(0, urlBuilder.length() - 1);

        // 发送请求
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        // 读取响应
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        // 解析响应
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(response.toString());
        if ("1".equals(jsonObject.getString("status"))) {
            com.alibaba.fastjson.JSONArray pois = jsonObject.getJSONArray("pois");
            if (pois.size() > 0) {
                com.alibaba.fastjson.JSONObject poi = pois.getJSONObject(0);
                Map<String, Object> details = new HashMap<>();
                details.put("name", translate(poi.getString("name")));
                details.put("phone", poi.getString("tel"));
                details.put("email", poi.getString("email"));
                details.put("business", poi.getString("business"));
                details.put("type", poi.getString("type"));
                return details;
            }
        }
        return null;
    }

    private String translate(String address){
        try {
            // 构建润色请求参数
            GenerationParam param = GenerationParam.builder()
                    .apiKey(QWEN_API_KEY)
                    .model(Generation.Models.QWEN_TURBO)
                    .prompt("请帮我翻译为英文：\n" + address)
                    .maxTokens(2048)
                    .temperature(0.7f)
                    .build();

            // 调用千问AI
            Generation generation = new Generation();
            GenerationResult result = generation.call(param);

            // 处理响应
            if (result.getOutput() != null && result.getOutput().getText() != null) {
                return result.getOutput().getText().trim();
            } else {
                return address;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return address;
        }
    }

    private String generateSignature(Map<String, String> params, String privateKey) {
        try {
            TreeMap<String, String> sortedParams = new TreeMap<>(params);

            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }

            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }

            sb.append(privateKey);

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(sb.toString().getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            System.err.println("generate sign fail: " + e.getMessage());
            return "";
        }
    }

    @PostMapping("/geocode/searchPetHospitals")
    public ReturnResp searchPetHospitals(@RequestBody Map<String, Object> request) {
        try {
            Double latitude = (Double) request.get("latitude");
            Double longitude = (Double) request.get("longitude");
            Integer radius = (Integer) request.get("radius");

            if (latitude == null || longitude == null) {
                return new ReturnResp(400, "Latitude and longitude are required", null);
            }

            if (radius == null) {
                radius = 30000;
            }

            Map<String, String> params = new TreeMap<>();
            params.put("location", longitude + "," + latitude);
            params.put("keywords", "宠物医院|宠物诊所|动物医院|动物诊所|veterinary|pet hospital");
            params.put("radius", String.valueOf(radius));
            params.put("extensions", "all");
            params.put("output", "json");
            params.put("key", WEB_SERVICE_KEY);
            params.put("lang", "en");
            params.put("sortrule", "distance");

            String signature = generateSignature(params, SECRET);
            params.put("sig", signature);

            StringBuilder urlBuilder = new StringBuilder("https://restapi.amap.com/v3/place/around?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
            }
            String urlStr = urlBuilder.toString().substring(0, urlBuilder.length() - 1);

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(response.toString());
            if ("1".equals(jsonObject.getString("status"))) {
                com.alibaba.fastjson.JSONArray pois = jsonObject.getJSONArray("pois");
                if (pois != null && pois.size() > 0) {
                    List<Map<String, Object>> results = new ArrayList<>();
                    for (int i = 0; i < Math.min(pois.size(), 20); i++) {
                        com.alibaba.fastjson.JSONObject poi = pois.getJSONObject(i);
                        String location = poi.getString("location");
                        String[] coords = location.split(",");
                        String name = translate(poi.getString("name"));
                        String address = translate(poi.getString("address"));
                        Double pLong = Double.parseDouble(coords[0]);
                        Double pLat = Double.parseDouble(coords[1]);
                        String tel = poi.getString("tel");

                        Map<String, Object> result = new HashMap<>();
                        result.put("name", name);
                        result.put("address", address);
                        result.put("longitude", pLong);
                        result.put("latitude", pLat);
                        result.put("phone", tel);
                        result.put("type", poi.getString("type"));
                        result.put("business", poi.getString("business"));
                        results.add(result);

                        RescueStation station = new RescueStation();
                        station.setName(name);
                        station.setAddress(address);
                        station.setLongitude(pLong);
                        station.setLatitude(pLat);
                        station.setPhone(tel);
                        station.setDescription("Pet hospital auto-registered from POI search");
                        rescueStationMapper.insert(station);

                        User user = new User();
                        user.setUsername(name.replaceAll("[^a-zA-Z0-9]", "_"));
                        user.setName(name);
                        user.setPhone(tel);
                        user.setPassword("123456");
                        user.setRescueStationId(station.getId());
                        user.setRole(2);
                        user.setCreatedAt(new Date());
                        userMapper.insert(user);

                        result.put("id", station.getId());
                    }
                    return new ReturnResp(200, "Success", results);
                }
            }

            return new ReturnResp(404, "No pet hospitals found", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnResp(500, "Server error: " + e.getMessage(), null);
        }
    }
}
