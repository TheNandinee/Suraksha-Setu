package com.nandinee;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class QueryController {

    @GetMapping("/logs")
    public String getLogs() throws Exception {
        String esQuery = "http://localhost:9200/moderation-logs/_search?size=50&sort=ts:desc";
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(esQuery);
            get.setHeader("Content-Type","application/json");
            return EntityUtils.toString(client.execute(get).getEntity());
        }
    }
}
