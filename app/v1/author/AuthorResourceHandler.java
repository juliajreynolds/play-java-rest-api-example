package v1.author;

import com.palominolabs.http.url.UrlBuilder;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;

import javax.inject.Inject;
import java.nio.charset.CharacterCodingException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * Handles presentation of Author resources, which map to JSON.
 */
public class AuthorResourceHandler {

    private final AuthorRepository repository;
    private final HttpExecutionContext ec;

    @Inject
    public AuthorResourceHandler(AuthorRepository repository, HttpExecutionContext ec) {
        this.repository = repository;
        this.ec = ec;
    }

    public CompletionStage<Stream<AuthorResource>> find() {
        return repository.list().thenApplyAsync(authorDataStream -> {
            return authorDataStream.map(data -> new AuthorResource(data, link(data)));
        }, ec.current());
    }

    public CompletionStage<AuthorResource> create(AuthorResource resource) {
        final AuthorData data = new AuthorData(resource.getName(), resource.getBlurb());
        return repository.create(data).thenApplyAsync(savedData -> {
            return new AuthorResource(savedData, link(savedData));
        }, ec.current());
    }

    public CompletionStage<Optional<AuthorResource>> lookup(String id) {
        return repository.get(Long.parseLong(id)).thenApplyAsync(optionalData -> {
            return optionalData.map(data -> new AuthorResource(data, link(data)));
        }, ec.current());
    }

    public CompletionStage<Optional<AuthorResource>> update(String id, AuthorResource resource) {
        final AuthorData data = new AuthorData(resource.getName(), resource.getBlurb());
        return repository.update(Long.parseLong(id), data).thenApplyAsync(optionalData -> {
            return optionalData.map(op -> new AuthorResource(op, link(op)));
        }, ec.current());
    }

    private String link(AuthorData data) {
        // Make a point of using request context here, even if it's a bit strange
        final Http.Request request = Http.Context.current().request();
        final String[] hostPort = request.host().split(":");
        String host = hostPort[0];
        int port = (hostPort.length == 2) ? Integer.parseInt(hostPort[1]) : -1;
        final String scheme = request.secure() ? "https" : "http";
        try {
            return UrlBuilder.forHost(scheme, host, port)
                    .pathSegments("v1", "authors", data.id.toString())
                    .toUrlString();
        } catch (CharacterCodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
