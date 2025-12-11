package edu.ban7.estiam425back.service;

import edu.ban7.estiam425back.dao.PlatRepository;
import edu.ban7.estiam425back.model.Plat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${google.api.key}")
    private String apiKey;

    protected final PlatRepository platRepository;

    private final RestTemplate restTemplate;

    public List<List<Float>> generateEmbeddings(List<String> texts) {
        List<List<Float>> embeddings = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        for (String text : texts) {
            // Construisez la requête pour un seul texte
            EmbeddingRequest.Request requestBody = new EmbeddingRequest.Request();
            requestBody.setModel("models/gemini-embedding-001");

            EmbeddingRequest.Request.Content content = new EmbeddingRequest.Request.Content();
            EmbeddingRequest.Request.Content.Part part = new EmbeddingRequest.Request.Content.Part();
            part.setText(text);
            content.setParts(List.of(part));
            requestBody.setContent(content);

            // Créez la requête HTTP
            HttpEntity<EmbeddingRequest.Request> request = new HttpEntity<>(requestBody, headers);

            // Appelez l'API
            ResponseEntity<EmbeddingResponse> responseEntity = restTemplate.exchange(
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-embedding-001:embedContent",
                    HttpMethod.POST,
                    request,
                    EmbeddingResponse.class
            );

            // Extrayez l'embedding de la réponse
            EmbeddingResponse response = responseEntity.getBody();
            if (response != null && response.getEmbedding() != null) {
                embeddings.add(response.getEmbedding().getValues());
            }
        }

        return embeddings;
    }

    public List<Float> generateEmbedding(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        EmbeddingRequest.Request requestBody = new EmbeddingRequest.Request();
        requestBody.setModel("models/gemini-embedding-001");

        EmbeddingRequest.Request.Content content = new EmbeddingRequest.Request.Content();
        EmbeddingRequest.Request.Content.Part part = new EmbeddingRequest.Request.Content.Part();
        part.setText(text);
        content.setParts(List.of(part));
        requestBody.setContent(content);

        HttpEntity<EmbeddingRequest.Request> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<EmbeddingResponse> responseEntity = restTemplate.exchange(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-embedding-001:embedContent",
                HttpMethod.POST,
                request,
                EmbeddingResponse.class
        );

        EmbeddingResponse response = responseEntity.getBody();
        if (response != null && response.getEmbedding() != null) {
            return response.getEmbedding().getValues();
        } else {
            throw new RuntimeException("Failed to generate embedding for text: " + text);
        }
    }

    public Plat findMostSimilarPlat(String message) {
        // Générer l'embedding du message
        List<Float> messageEmbedding = generateEmbedding(message);

        // Récupérer tous les plats de la base de données
        List<Plat> plats = platRepository.findAll();

        // Trouver le plat avec l'embedding le plus similaire
        Plat mostSimilarPlat = null;
        double maxSimilarity = -1.0;

        for (Plat plat : plats) {
            List<Float> platEmbedding = plat.getEmbeddingAsList();
            double similarity = cosineSimilarity(messageEmbedding, platEmbedding);
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                mostSimilarPlat = plat;
            }
        }

        return mostSimilarPlat;
    }

    // Méthode pour calculer la similarité cosinus
    private double cosineSimilarity(List<Float> vec1, List<Float> vec2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            norm1 += Math.pow(vec1.get(i), 2);
            norm2 += Math.pow(vec2.get(i), 2);
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    @Getter
    @Setter
    public static class EmbeddingResponse {
        private Embedding embedding;

        @Getter
        @Setter
        public static class Embedding {
            private List<Float> values;
        }
    }

    @Getter
    @Setter
    public static class EmbeddingRequest {
        private Request request;

        @Getter
        @Setter
        public static class Request {
            private String model;
            private Content content;

            @Getter
            @Setter
            public static class Content {
                private List<Part> parts;

                @Getter
                @Setter
                public static class Part {
                    private String text;
                }
            }
        }
    }
}
