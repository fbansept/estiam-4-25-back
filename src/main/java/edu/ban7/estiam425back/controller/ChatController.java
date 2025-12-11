package edu.ban7.estiam425back.controller;

import edu.ban7.estiam425back.dto.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    public ResponseEntity<String> askAI(@RequestBody Message message) {

        System.out.println(message.getContenu());

        return ResponseEntity.ok("ok");

    }

}
