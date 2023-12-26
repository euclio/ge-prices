package io.eucl.geprices.wikiprices;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;

/**
 * Client for the <a href="https://oldschool.runescape.wiki/w/RuneScape:Real-time_Prices">Real-time Prices API</a>.
 */
@Slf4j
@RequiredArgsConstructor
public class RealTimePricesClient {

    private static final String USER_AGENT = "@euclio47 on Discord -- In-development Runelite Plugin";

    private final HttpClient client;

    public RealTimePricesClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    /**
     * Retrieves a list of high and low prices of an item with the given ID at the given interval.
     * @param itemId Item id to return a time-series for.
     * @param timestep Timestep of the time-seires.
     * @return Timeseries.
     *
     * @throws IOException if the request encountered an IO error
     * @throws InterruptedException if the request was interrupted
     */
    public CompletableFuture<Timeseries> getTimeseries(int itemId, @NonNull Timestep timestep) {
        final URI endpoint;

        try {
            endpoint = new URI(String.format(
                    "https://prices.runescape.wiki/api/v1/osrs/timeseries?timestep=%s&id=%s", timestep, itemId));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = HttpRequest.newBuilder(endpoint)
                .header("User-Agent", USER_AGENT)
                .GET()
                .build();

        log.debug("sending Real-time Prices request for item {}: {}", itemId, request);

        return CompletableFuture.supplyAsync(() -> {
            try {
                final HttpResponse<Supplier<Timeseries>> response = client.send(request, new JsonBodyHandler<>(Timeseries.class));
                return response.body().get();
            } catch (InterruptedException | IOException e) {
                throw new CompletionException(e);
            }
        });
    }
}
