package com.crisis360.crisis360_core.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class MlRiskClient {

    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String BASE = "https://dileepamalshan-crisis360.hf.space";
    private static final String PREDICT_PATH = "/gradio_api/call/predict_risk";

    public MlRiskClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl(BASE).build();
    }

    /**
     * Returns: LOW | MEDIUM | HIGH | UNKNOWN
     */
    public String predictRiskLevel(String message) {
        try {
            String msg = (message == null) ? "" : message.trim();
            if (msg.isEmpty()) return "UNKNOWN";

            // ✅ IMPORTANT: send as real JSON object (Map), not String
            Map<String, Object> postBody = Map.of("data", List.of(msg));

            String postResp = webClient.post()
                    .uri(PREDICT_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(postBody)
                    .retrieve()
                    .onStatus(
                            status -> status.value() == 422,
                            resp -> resp.bodyToMono(String.class).flatMap(body -> {
                                System.out.println("[MlRiskClient] 422 from HF POST. Body: " + body);
                                return Mono.error(new RuntimeException("HF POST 422: " + body));
                            })
                    )
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(25))
                    .block();

            if (postResp == null || postResp.isBlank()) return "UNKNOWN";

            String eventId = extractEventId(postResp);
            if (eventId == null) return "UNKNOWN";

            String getResp = webClient.get()
                    .uri(PREDICT_PATH + "/" + eventId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            if (getResp == null || getResp.isBlank()) return "UNKNOWN";

            return extractRiskLevel(getResp);

        } catch (Exception e) {
            System.out.println("[MlRiskClient] ML failed, returning UNKNOWN. Reason: " + e.getMessage());
            return "UNKNOWN";
        }
    }

    private String extractEventId(String postResp) {
        try {
            JsonNode j = mapper.readTree(postResp);
            if (j.has("event_id")) return j.get("event_id").asText();
            if (j.has("id")) return j.get("id").asText();
        } catch (Exception ignored) {}

        // fallback: search in text
        int idx = postResp.indexOf("\"event_id\":\"");
        if (idx >= 0) {
            int start = idx + "\"event_id\":\"".length();
            int end = postResp.indexOf("\"", start);
            if (end > start) return postResp.substring(start, end);
        }
        return null;
    }

    private String extractRiskLevel(String raw) {
        try {
            // Sometimes Gradio returns SSE text; try to find JSON array start
            int start = raw.indexOf('[');
            String jsonPart = (start >= 0) ? raw.substring(start) : raw;

            JsonNode root = mapper.readTree(jsonPart);

            JsonNode arr = root.isArray() ? root
                    : root.has("data") ? root.get("data")
                    : root.has("output") ? root.get("output")
                    : null;

            if (arr == null || !arr.isArray() || arr.size() == 0) return "UNKNOWN";

            JsonNode first = arr.get(0);
            JsonNode rl = first.get("Risk Level");
            if (rl == null) rl = first.get("risk_level");
            if (rl == null) rl = first.get("riskLevel");

            if (rl == null) return "UNKNOWN";

            String v = rl.asText("").trim().toUpperCase();
            if (v.equals("LOW") || v.equals("MEDIUM") || v.equals("HIGH")) return v;

            return "UNKNOWN";

        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}