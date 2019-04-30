package v1.author;

import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.Failsafe;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A repository that provides a non-blocking API with a custom execution context
 * and circuit breaker.
 */
@Singleton
public class JPAAuthorRepository implements AuthorRepository {

    private final JPAApi jpaApi;
    private final AuthorExecutionContext ec;
    private final CircuitBreaker circuitBreaker = new CircuitBreaker().withFailureThreshold(1).withSuccessThreshold(3);

    @Inject
    public JPAAuthorRepository(JPAApi api, AuthorExecutionContext ec) {
        this.jpaApi = api;
        this.ec = ec;
    }

    @Override
    public CompletionStage<Stream<AuthorData>> list() {
        return supplyAsync(() -> wrap(em -> select(em)), ec);
    }

    @Override
    public CompletionStage<AuthorData> create(AuthorData authorData) {
        return supplyAsync(() -> wrap(em -> insert(em, authorData)), ec);
    }

    @Override
    public CompletionStage<Optional<AuthorData>> get(Long id) {
        return supplyAsync(() -> wrap(em -> Failsafe.with(circuitBreaker).get(() -> lookup(em, id))), ec);
    }

    @Override
    public CompletionStage<Optional<AuthorData>> update(Long id, AuthorData authorData) {
        return supplyAsync(() -> wrap(em -> Failsafe.with(circuitBreaker).get(() -> modify(em, id, authorData))), ec);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Optional<AuthorData> lookup(EntityManager em, Long id) throws SQLException {
        throw new SQLException("Call this to cause the circuit breaker to trip");
        //return Optional.ofNullable(em.find(AuthorData.class, id));
    }

    private Stream<AuthorData> select(EntityManager em) {
        TypedQuery<AuthorData> query = em.createQuery("SELECT p FROM PostData p", AuthorData.class);
        return query.getResultList().stream();
    }

    private Optional<AuthorData> modify(EntityManager em, Long id, AuthorData authorData) throws InterruptedException {
        final AuthorData data = em.find(AuthorData.class, id);
        if (data != null) {
            data.name = authorData.name;
            data.blurb = authorData.blurb;
        }
        Thread.sleep(10000L);
        return Optional.ofNullable(data);
    }

    private AuthorData insert(EntityManager em, AuthorData authorData) {
        return em.merge(authorData);
    }
}
