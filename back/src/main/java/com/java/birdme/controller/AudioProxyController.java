package com.java.birdme.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequestMapping("/api/audio")
public class AudioProxyController {

    /**
     * Proxy endpoint to fetch audio files from xeno-canto CDN
     * This bypasses CORS and network restrictions
     */
    @GetMapping("/proxy")
    public void proxyAudio(@RequestParam("url") String audioUrl, HttpServletResponse response) throws IOException {
        try {
            // Ensure the URL is from xeno-canto CDN (security check)
            if (!audioUrl.startsWith("https://cdn.xeno-canto.org/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid audio source");
                return;
            }

            // Create connection to the audio file
            URL url = new URL(audioUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            // Set user agent to avoid being blocked
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

            // Check response code
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Audio file not found");
                return;
            }

            // Set response content type
            String contentType = connection.getContentType();
            if (contentType == null) {
                contentType = "audio/mpeg";
            }
            response.setContentType(contentType);
            response.setHeader("Accept-Ranges", "bytes");

            // Stream the audio data to client
            try (InputStream inputStream = connection.getInputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    response.getOutputStream().write(buffer, 0, bytesRead);
                }
                response.getOutputStream().flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Failed to fetch audio: " + e.getMessage());
        }
    }

    /**
     * Alternative using RestTemplate (simpler)
     */
    @GetMapping("/fetch")
    public ResponseEntity<byte[]> fetchAudio(@RequestParam("url") String audioUrl) {
        try {
            // Security check
            if (!audioUrl.startsWith("https://cdn.xeno-canto.org/")) {
                return ResponseEntity.badRequest().build();
            }

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    audioUrl,
                    HttpMethod.GET,
                    null,
                    byte[].class
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "audio/mpeg");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}