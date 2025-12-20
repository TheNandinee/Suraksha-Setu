package com.nandinee.ingress.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
public class MessageController {

    private final KafkaTemplate<String,String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${app.topic.incoming}")
    private String incomingTopic;

    public MessageController(KafkaTemplate<String,String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/messages")
    public String ingest(@RequestBody HashMap<String,Object> payload) throws Exception {
        payload.putIfAbsent("id", UUID.randomUUID().toString());
        payload.putIfAbsent("receivedAt", System.currentTimeMillis());
        String json = mapper.writeValueAsString(payload);
        kafkaTemplate.send(incomingTopic, payload.get("id").toString(), json);
        return "{\"status\":\"accepted\",\"id\":\"" + payload.get("id").toString() + "\"}";
    }
}
