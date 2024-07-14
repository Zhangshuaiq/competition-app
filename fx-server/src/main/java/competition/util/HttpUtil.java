package competition.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Map;

public class HttpUtil {

    public static String sendGetRequest(String url) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return handleResponse(response);
            }
        }
    }

    public static String sendPostRequest(String url, String jsonBody) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(jsonBody));
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return handleResponse(response);
            }
        }
    }

    public static String sendPostRequest(String url, Map<String, String> formParams) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);
            request.setHeader("Content-Type", "application/x-www-form-urlencoded");

            StringBuilder formBody = new StringBuilder();
            for (Map.Entry<String, String> entry : formParams.entrySet()) {
                if (formBody.length() > 0) {
                    formBody.append("&");
                }
                formBody.append(entry.getKey()).append("=").append(entry.getValue());
            }
            request.setEntity(new StringEntity(formBody.toString()));
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return handleResponse(response);
            }
        }
    }

    private static String handleResponse(HttpResponse response) throws Exception {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new RuntimeException("HTTP request failed with status code: " + statusCode);
        }
        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }

//    public static void main(String[] args) {
//        try {
//            String getResponse = HttpClientUtil.sendGetRequest("https://jsonplaceholder.typicode.com/posts/1");
//            System.out.println("GET Response: " + getResponse);
//
//            String postResponse = HttpClientUtil.sendPostRequest("https://jsonplaceholder.typicode.com/posts",
//                    "{ \"title\": \"foo\", \"body\": \"bar\", \"userId\": 1 }");
//            System.out.println("POST Response (JSON): " + postResponse);
//
//            String formPostResponse = HttpClientUtil.sendPostRequest("https://example.com/api",
//                    Map.of("param1", "value1", "param2", "value2"));
//            System.out.println("POST Response (Form): " + formPostResponse);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
