package v1.author;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface AuthorRepository {

    CompletionStage<Stream<AuthorData>> list();

    CompletionStage<AuthorData> create(AuthorData authorData);

    CompletionStage<Optional<AuthorData>> get(Long id);

    CompletionStage<Optional<AuthorData>> update(Long id, AuthorData authorData);
}

