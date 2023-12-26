package io.eucl.geprices.wikiprices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.util.function.Supplier;

/**
 * Body handler that deserialized a JSON object.
 *
 * <p>
 * The deserialized object is returned in a {@link Supplier} to work around a <a href="https://stackoverflow.com/a/57630142">bug in JDK 11</a>.
 * @param <T> The type to deserialize.
 */
public class JsonBodyHandler<T> implements HttpResponse.BodyHandler<Supplier<T>> {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private final Class<T> bodyClass;

    public JsonBodyHandler(Class<T> bodyClass) {
        this.bodyClass = bodyClass;
    }

    @Override
    public HttpResponse.BodySubscriber<Supplier<T>> apply(HttpResponse.ResponseInfo responseInfo) {
        HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();

        return HttpResponse.BodySubscribers.mapping(upstream, bodyStream -> {
            return () -> {
                try {
                    return MAPPER.readValue(bodyStream, this.bodyClass);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            };
        });
    }
}
