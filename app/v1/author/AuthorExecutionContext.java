package v1.author;

import akka.actor.ActorSystem;
import play.libs.concurrent.CustomExecutionContext;

import javax.inject.Inject;

/**
 * Custom execution context wired to "author.repository" thread pool
 */
public class AuthorExecutionContext extends CustomExecutionContext {

    @Inject
    public AuthorExecutionContext(ActorSystem actorSystem) {
        super(actorSystem, "author.repository");
    }
}
