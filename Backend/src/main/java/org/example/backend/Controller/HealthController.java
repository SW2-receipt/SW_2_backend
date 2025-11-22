package org.example.backend.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller
 * Simple endpoint to check if the server is running
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Server is running");
        return response;
    }

    @GetMapping("/")
    @ResponseBody
    public String root() {
        // 간단한 HTML 로그인 페이지 반환
        return "<!DOCTYPE html>" +
                "<html lang='ko'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>로그인</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; background-color: #f5f5f5; }" +
                ".container { background: white; padding: 40px; border-radius: 12px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); text-align: center; }" +
                "h1 { color: #333; margin-bottom: 10px; }" +
                "p { color: #666; margin-bottom: 30px; }" +
                ".login-button { display: inline-block; background-color: #FEE500; color: #000; padding: 14px 28px; border-radius: 12px; text-decoration: none; font-weight: 600; font-size: 16px; border: 1px solid #FEE500; }" +
                ".login-button:hover { background-color: #FDD835; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h1>가계부 로그인</h1>" +
                "<p>카카오 계정으로 로그인하세요</p>" +
                "<a href='/oauth2/authorization/kakao' class='login-button'>카카오로 로그인</a>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}

