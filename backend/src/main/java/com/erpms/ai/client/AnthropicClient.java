package com.erpms.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

/**
 * Thin, blocking client for Anthropic's Messages API.
 *
 * <p>Chosen deliberately over WebClient for simplicity — reactive streams are
 * overkill for the ERPMS use case (a handful of LLM calls per user session).
 * The API key, base URL and model are all externalised so the operator can
 * point the client at a self-hosted proxy or a different model without a
 * code change.
 */
@Component
public class AnthropicClient {

    private static final Logger log = LoggerFactory.getLogger(AnthropicClient.class);

    private final RestClient restClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String apiKey;
    private final String model;
    private final int maxTokens;
    private final boolean enabled;

    public AnthropicClient(
            @Value("${erpms.ai.anthropic.api-key:}") String apiKey,
            @Value("${erpms.ai.anthropic.base-url:https://api.anthropic.com}") String baseUrl,
            @Value("${erpms.ai.anthropic.model:claude-sonnet-4-5-20250929}") String model,
            @Value("${erpms.ai.anthropic.max-tokens:2048}") int maxTokens,
            @Value("${erpms.ai.anthropic.version:2023-06-01}") String anthropicVersion,
            @Value("${erpms.ai.anthropic.timeout-seconds:60}") int timeoutSeconds
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.maxTokens = maxTokens;
        this.enabled = apiKey != null && !apiKey.isBlank();

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(timeoutSeconds).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(timeoutSeconds).toMillis());

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("anthropic-version", anthropicVersion)
                .build();

        if (!enabled) {
            log.warn("[ai] Anthropic client is DISABLED (erpms.ai.anthropic.api-key is not set) — "
                    + "AI endpoints will return a canned response.");
        }
    }

    public boolean isEnabled() { return enabled; }

    /**
     * Send a single-turn user message with an optional system prompt.
     * @return the assistant's plain-text reply
     */
    public String chat(String systemPrompt, String userMessage) {
        return chatMulti(systemPrompt, List.of(Map.of("role", "user", "content", userMessage)));
    }

    /**
     * Send a multi-turn conversation. Each element in {@code messages} must be
     * a map of {@code role} (user / assistant) and {@code content}.
     */
    public String chatMulti(String systemPrompt, List<Map<String, String>> messages) {
        if (!enabled) {
            return "(AI is not configured on this environment. Set erpms.ai.anthropic.api-key to enable Claude.)";
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("max_tokens", maxTokens);
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            body.put("system", systemPrompt);
        }
        body.put("messages", messages);
        try {
            String json = restClient.post()
                    .uri("/v1/messages")
                    .header("x-api-key", apiKey)
                    .body(body)
                    .retrieve()
                    .body(String.class);
            JsonNode root = mapper.readTree(json);
            JsonNode content = root.path("content");
            if (content.isArray() && !content.isEmpty()) {
                return content.get(0).path("text").asText();
            }
            return "";
        } catch (HttpClientErrorException ex) {
            log.error("[ai] Anthropic API error ({}): {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            return "AI service returned an error: " + ex.getStatusCode();
        } catch (Exception ex) {
            log.error("[ai] Anthropic call failed: {}", ex.getMessage(), ex);
            return "AI service is currently unavailable.";
        }
    }
}
