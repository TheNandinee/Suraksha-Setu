package com.nandinee;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ModerationListener {

    @Value("${app.ml.url}")
    private String mlUrl;

    @Value("${app.topic.actions}")
    private String actionsTopic;

    @Value("${app.elastic.url}")
    private String elasticUrl;

    private final KafkaTemplate<String,String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public ModerationListener(KafkaTemplate<String,String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "${app.topic.incoming}", groupId = "moderation-group")
    public void onMessage(String message) {
        try {
            Map payload = mapper.readValue(message, Map.class);
            String text = payload.get("text") != null ? payload.get("text").toString() : "";

            Map scores = callMlService(text);

            double toxicity = 0.0;
            if (scores.get("toxicity") != null) {
                Object o = scores.get("toxicity");
                if (o instanceof Number) toxicity = ((Number)o).doubleValue();
                else toxicity = Double.parseDouble(o.toString());
            }

            boolean blocked = toxicity >= 0.6;

            Map action = new HashMap();
            action.put("id", payload.get("id"));
            action.put("text", text);
            action.put("scores", scores);
            action.put("blocked", blocked);
            action.put("ts", System.currentTimeMillis());

            String actionJson = mapper.writeValueAsString(action);

            kafkaTemplate.send(actionsTopic, payload.get("id").toString(), actionJson);

            indexToElastic(actionJson);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Map callMlService(String text) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(mlUrl);
            post.setHeader("Content-Type","application/json");
            Map body = new HashMap();
            body.put("text", text);
            post.setEntity(new StringEntity(mapper.writeValueAsString(body), "UTF-8"));
            String respStr = EntityUtils.toString(client.execute(post).getEntity(), "UTF-8");
            Map m = mapper.readValue(respStr, Map.class);
            return m;
        }
    }

    private void indexToElastic(String json) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(elasticUrl);
            post.setHeader("Content-Type","application/json");
            post.setEntity(new StringEntity(json, "UTF-8"));
            client.execute(post).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
