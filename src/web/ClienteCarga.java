package web;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ClienteCarga {
    public static void main(String[] args) throws Exception {
        HttpClient cliente = HttpClient.newHttpClient();
        List<CompletableFuture<?>> pendientes = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            for (String ruta : new String[] { "/visita", "/visita-segura" }) {
                HttpRequest p = HttpRequest.newBuilder(
                    URI.create("http://localhost:8080" + ruta)).build();
                pendientes.add(cliente.sendAsync(p, HttpResponse.BodyHandlers.discarding()));
            }
        }
        CompletableFuture.allOf(pendientes.toArray(new CompletableFuture[0])).join();
        HttpRequest conteo = HttpRequest.newBuilder(
            URI.create("http://localhost:8080/conteo")).build();
        System.out.println(cliente.send(conteo,
            HttpResponse.BodyHandlers.ofString()).body());
    }
}
