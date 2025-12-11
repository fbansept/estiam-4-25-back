package edu.ban7.estiam425back.controller;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import edu.ban7.estiam425back.dto.Message;
import edu.ban7.estiam425back.model.Plat;
import edu.ban7.estiam425back.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    @Value("${google.api.key}")
    protected String apiKey;

    protected final GeminiService geminiService;

    @PostMapping("/askAI")
    public ResponseEntity<String> askAI(@RequestBody Message message) {

        System.out.println(message.getContenu());

        //note API ou LLM interne

        //premier solution
        // Envoyer le message a un autre chatbot avec un prompt spécifique
        // contexte :connaitre le nombre de fautes d'orthographe dans une phrase
        //ex : "Combien de faute d'orthographe trouves-tu dans ce message : '" + message.getContenu() + "'" +
        //" . Ne répond que par le nombre de faute."

//        Client client = Client.builder()
//                .apiKey(apiKey)
//                .build();
//
//        GenerateContentResponse response =
//                client.models.generateContent(
//                        "gemini-2.5-flash",
//                        "Combien il y a t-il de fautes d'orthographe dans ce texte : '" + message.getContenu() + "'. répond juste sous cette forme : 'il y a x erreurs'",
//                        null);
//
//        return ResponseEntity.ok(response.text());

        //deuxieme solution RAG (retrieval augmented generation)
        //Utiliser une table vectorielle. pour un systeme Question / réponse
        // contexte : obtenir des infos sur un plat
        // l'idée c'est de trouver une similarité entre des mots "ex : pate et noodle, allergène et intolérance"
        //il va faloir a chaque nouvelle entreé (nos plat) préciser les info comme le prix, les allergène, la description etc...
        //pour ensuite utiliser une IA pour les vectoriser
        //chaque comparaison, devra au préable vectoriser la recherche du client.

        Plat platSimilaire = geminiService.findMostSimilarPlat(message.getContenu());

        return ResponseEntity.ok("Nous vous proposons : " + platSimilaire.getNom() + " pour " + platSimilaire.getPrix() + "€");


        //Solution hybride
        //On recherche dans une table vectorielle, et on envoie le resultat a une ia, pour qu'elle structure la reponse
        // selon ce resultat (ex : "quels sont vos pâte les moins chere" , la table retourne les ligne concernant les
        // "noodle" et les "tagiatelle" (avec toutes les infos y compris le prix). Et l'IA structure la réponse en
        // mentionnant "les noodle à 5 €"

        //3eme solution
        //entrainer sont propre LLM , mais les données sont figées à celle entrainée.
        //il faut un jeu de donnée très conséquent et le cout initial est plus que couteux

        //4eme solution (plutôt hybride)
        //avoir une simple logique métier


        //Hors sujet
        //Serveur MCP (communication inter IA) plutot que de communiquer entre API rigide, les IA communique
        // entre elle pour s'envoyer des données

        //Automatisation (N8N)
        //attention ca reste payant


    }

}
