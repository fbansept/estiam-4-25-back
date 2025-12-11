package edu.ban7.estiam425back.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import edu.ban7.estiam425back.dao.PlatRepository;
import edu.ban7.estiam425back.model.Plat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class InitializerVectorTable {

    @Autowired
    private PlatRepository platRepository;

    @Autowired
    private GeminiService geminiService;

    @Transactional
    public void importPlatsFromCSV(String filePath) throws IOException, CsvException {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> rows = reader.readAll();
            // Ignorer l'en-tête
            List<String> texts = new ArrayList<>();
            List<Plat> plats = new ArrayList<>();

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                Plat plat = new Plat();
                plat.setNom(row[0]);
                plat.setPrix(Double.parseDouble(row[1]));
                plat.setDescription(row[2]);
                plat.setAllergenes(row[3]);

                // Ajoutez le texte à la liste pour le batch
                texts.add(plat.getNom() + " " + plat.getDescription());
                plats.add(plat);
            }

            // Générez les embeddings pour tous les textes en une seule requête
            List<List<Float>> embeddings = geminiService.generateEmbeddings(texts);

            // Assignez les embeddings aux plats
            for (int i = 0; i < plats.size(); i++) {
                Plat plat = plats.get(i);
                plat.setEmbeddingFromList(embeddings.get(i));
                platRepository.save(plat);
            }
        }

    }
}
