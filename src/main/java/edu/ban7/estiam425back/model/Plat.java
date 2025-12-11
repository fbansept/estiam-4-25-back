package edu.ban7.estiam425back.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "plats")
@Getter
@Setter
public class Plat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private double prix;
    private String description;
    private String allergenes;

    @Column(name = "embedding", columnDefinition = "vector(3072)")
    @ColumnTransformer(write = "?::vector")
    private String embedding;

    // Méthodes utilitaires pour les embeddings
    public void setEmbeddingFromList(List<Float> embeddingList) {
        this.embedding = embeddingList.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",", "[", "]"));
    }

    public List<Float> getEmbeddingAsList() {
        if (this.embedding == null || this.embedding.isEmpty()) {
            return List.of();
        }
        String s = this.embedding.replace("[", "").replace("]", "");
        return Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(part -> !part.isEmpty())
                .map(Float::parseFloat)
                .collect(Collectors.toList());
    }
}
