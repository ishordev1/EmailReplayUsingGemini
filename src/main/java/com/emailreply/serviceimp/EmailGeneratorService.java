package com.emailreply.serviceimp;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.emailreply.entity.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmailGeneratorService {

	@Value("${gemini.api.url}")
	private String geminiApiUrl;
	@Value("${gemini.api.key}")
	private String geminiApiKey;
	
	private final WebClient webClient;

	EmailGeneratorService(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.build();
	}

	public String generateEmailReply(EmailRequest emailRequest) {
		// 1. build the prompt
		String prompt = buildPrompt(emailRequest);
		
		// 2. craft the request
		/*
		 * { "contents": [{ "parts":[{"text": "Explain how AI works"}] }] }
		 */
		Map<String, Object> requestBody = Map.of("contents",
				new Object[] { Map.of("parts", new Object[] { Map.of("text", prompt) }) });

		// 3. do request and get Response
		// post because post request
		String response = webClient.post().uri(geminiApiUrl + geminiApiKey).header("Content-Type", "application/json")
				.bodyValue(requestBody)
				.retrieve().bodyToMono(String.class).block();

		// 4. Extratc Response and return Response
		return extractResponseContent(response);

	}

	private String extractResponseContent(String response) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(response);
			/*
			 * { "candidates": [ { "content": { "parts": [ { "text": "AI, or Artificial
			 * Intelligence, works by mimicking human cognitive functions,
			 */

			return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String buildPrompt(EmailRequest emailRequest) {
		StringBuilder prompt = new StringBuilder();
		prompt.append(
				"Generate a professional email reply for the following email content. Please don't generate subject line ");
		if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
			prompt.append("Use a").append(emailRequest.getTone()).append(" tone.");
		}
		prompt.append("\n Original email:\n").append(emailRequest.getEmailContent());
		return prompt.toString();
	}

}
