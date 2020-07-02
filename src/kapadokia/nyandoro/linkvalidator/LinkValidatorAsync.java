package kapadokia.nyandoro.linkvalidator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class LinkValidatorAsync {
    //initialize HTTPClient
    private static HttpClient client;

    //the main activity, throwing IOException
    public static void main (String [] args) throws IOException {
        // create an httpClient;
        // newHttpClient() - gives us a httpClient with all default settings
        client =  HttpClient.newHttpClient();

        // the below piece of code ensures that later on all the requests are executed in parallel
        // and we get back the list of completable futures that represents completable result.
        var futures =Files.lines(Path.of("urls.txt"))
                .map(LinkValidatorAsync::validateLink)
                .collect(Collectors.toList());

        //ensuring that the program doesn't end before the list of completable futures
        //are asynchronously completed
        // we do this by mapping completable future join over each completable future
        // that we got back from the validate link method
        futures.stream()
                .map(CompletableFuture::join)
                .forEach(System.out::println);

    }

    //validate string method
    // returns a completable future of string
    private static CompletableFuture<String> validateLink(String link){
        HttpRequest request = HttpRequest.newBuilder(URI.create(link))
                .GET()
                .build();



        return client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenApply(LinkValidatorAsync::responseToString)
                .exceptionally(e ->String.format("%s -> %s" ,link, false));
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
