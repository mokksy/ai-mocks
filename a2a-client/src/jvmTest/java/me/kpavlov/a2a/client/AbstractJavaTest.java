package me.kpavlov.a2a.client;

import me.kpavlov.aimocks.a2a.MockAgentServer;
import org.junit.jupiter.api.AfterEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for Java tests that use the A2A client.
 */
abstract class AbstractJavaTest {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final MockAgentServer a2aServer = new MockAgentServer(0, true);

    // Create the client with the baseUrl parameter and a Json object from the JavaTestHelper
    protected final A2AClient client = A2AClientFactory.create(a2aServer.baseUrl(), null, JavaTestHelper.createJson());

    @AfterEach
    public void afterEach() {
        a2aServer.verifyNoUnmatchedRequests();
    }
}
