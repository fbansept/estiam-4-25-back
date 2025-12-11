package edu.ban7.estiam425back;

import com.opencsv.exceptions.CsvException;
import edu.ban7.estiam425back.service.InitializerVectorTable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class Estiam425BackApplication {

    @Autowired
    protected InitializerVectorTable initializerVectorTable;

    public static void main(String[] args) {
        SpringApplication.run(Estiam425BackApplication.class, args);
    }

    @PostConstruct
    public void init() {

        //a décommenter pour initialiser la base via le CVS

//        try {
//            initializerVectorTable.importPlatsFromCSV("plats_restaurant.csv");
//        } catch (CsvException e) {
//            System.out.println("Erreur lecture fichier");
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            System.out.println("Erreur format CSV");
//            throw new RuntimeException(e);
//        }
    }

}
