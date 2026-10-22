package facade;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class NetworkSender {
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    // boilerplate code from MasteryLS docs
    // modified for this project
    public static HttpResponse<String> sendRequest(String url, Methods method, String[] headers, String body)
            throws InterruptedException, IOException {
        var request = HttpRequest.newBuilder(URI.create(url))
                .method(method.name(), requestBodyPublisher(body))
                .headers(headers)
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public enum Methods {
        GET,
        POST,
        PUT,
        DELETE
    }

    private static HttpRequest.BodyPublisher requestBodyPublisher(String body) {
        if (body != null) {
            return HttpRequest.BodyPublishers.ofString(body);
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }
}
