package ru.afina.accountant.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.afina.accountant.service.HazelcastRateLimiterService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
public class RateLimitController {

    private final HazelcastRateLimiterService rateLimiterService;

    @GetMapping("/check-request")
    public ResponseEntity<Map<String, Object>> checkRequest(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        String instanceId = System.getenv("HOSTNAME"); // ID контейнера

        boolean allowed = rateLimiterService.isAllowed(clientIp);
        HazelcastRateLimiterService.RateLimitStatus status = rateLimiterService.getStatus(clientIp);

        Map<String, Object> response = new HashMap<>();
        response.put("instance", instanceId);       // Какой инстанс обработал
        response.put("clientIp", clientIp);
        response.put("allowed", allowed);
        response.put("currentRequests", status.getCurrentRequests());
        response.put("remainingRequests", status.getRemainingRequests());
        response.put("limit", 100);
        response.put("windowSeconds", 60);

        return allowed ?
                ResponseEntity.ok(response) :
                ResponseEntity.status(429).body(response); // 429 Too Many Requests
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.split(",")[0].trim();
    }

}
