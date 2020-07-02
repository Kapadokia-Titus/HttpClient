package kapadokia.nyandoro.linkvalidator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class LinkValidatorSync {

    //initialize HTTPClient
    private static HttpClient client;

    //the main activity, throwing IOException
    public static void main (String [] args) throws IOException{
        // create an httpClient;
        // newHttpClient() - gives us a httpClient with all default settings
        client =  HttpClient.newHttpClient();
        Files.lines(Path.of("urls.txt"))
                .map(LinkValidatorSync::validateLink)
                .forEach(System.out::println);

    }

    //validate string method
    // returns a string
    private static String validateLink(String link){
        HttpRequest request = HttpRequest.newBuilder(URI.create(link))
                .GET()
                .build();

        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            return responseToString(response);
        }catch (IOException  | InterruptedException e){
            return String.format("%s -> %s" ,link, false);
        }
    }

    // gets a url response
    // checks if the status in the 200 range
    // then gives us a formatted string response.
    private static String responseToString(HttpResponse<Void> response){
        int status = response.statusCode();
        boolean success  = status>=200 && status<=299;
        return String.format("%s-> %s (status: %s)", response.uri(), success, status);
    }
}
