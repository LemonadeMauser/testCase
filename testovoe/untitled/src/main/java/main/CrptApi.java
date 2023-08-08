package main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class CrptApi {
    private final int requestLimiter;
    private final long timeInterval;
    private int requestCounter = 0;
    private long lastRequestTime = System.currentTimeMillis();

    private final HttpClient httpClient = HttpClients.createDefault();
    private final static ObjectMapper objectMapper = new ObjectMapper();

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.requestLimiter = requestLimit;
        this.timeInterval = timeUnit.toMillis(1);
    }

    synchronized public void createDocument(Object document, String signature) throws IOException, InterruptedException {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRequestTime > timeInterval) {
            requestCounter = 0;
            lastRequestTime = currentTime;
        }

        if (requestCounter >= requestLimiter) {
            long sleepTime = lastRequestTime + timeInterval - currentTime;
            if (sleepTime > 0) {
                Thread.sleep(sleepTime);
            }
            requestCounter = 0;
            lastRequestTime = System.currentTimeMillis();
        }

        requestCounter++;

        DocumentRequest request = new DocumentRequest();
        request.setDocumentFormat("MANUAL");
        request.setProductDocument(document);
        request.setProductGroup("pharma");
        request.setSignature(signature);
        request.setType("LP_INTRODUCE_GOODS");

        String requestBody = objectMapper.writeValueAsString(request);

        HttpPost httpPost = new HttpPost("https://ismp.crpt.ru/api/v3/lk/documents/create");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Bearer <ТОКЕН>");
        httpPost.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));

        HttpResponse response = httpClient.execute(httpPost);
        int status = response.getStatusLine().getStatusCode();
        if(status == 200){
            System.out.println("Запрос отправен");
        } else {
            System.out.println("Ошибка запроса. Код ошибки -  " + status);
        }
    }

    public static void main(String[] args) {
        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 5);

        Runnable task = () -> {
            try {
                crptApi.createDocument(new Object(), "signature");
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        };

        for (int i = 0; i < 10; i++) {
            new Thread(task).start();
        }
    }

    private static class DocumentRequest {
        private String documentFormat;
        private String productDocument;
        private String signature;
        private String type;
        private String productGroup;

        public void setType(String type) {
            this.type = type;
        }

        public void setProductGroup(String productGroup) {
            this.productGroup = productGroup;
        }

        public void setDocumentFormat(String documentFormat) {
            this.documentFormat = documentFormat;
        }

        public void setProductDocument(Object productDocument) throws JsonProcessingException {
            String jsonString = objectMapper.writeValueAsString(productDocument);
            this.productDocument = Base64.getEncoder().encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getDocumentFormat() {
            return documentFormat;
        }

        public String getProductDocument() {
            return productDocument;
        }

        public String getSignature() {
            return signature;
        }

        public String getType() {
            return type;
        }

        public String getProductGroup() {
            return productGroup;
        }
    }
}