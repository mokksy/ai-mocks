package dev.mokksy.a2a.client;

import dev.mokksy.aimocks.a2a.model.*;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.mokksy.a2a.client.A2AClient_jvmKt.sendStreamingMessageAsJavaFlow;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Java version of the SendTaskStreamingTest.
 * See <a href="https://a2a-protocol.org/latest/specification/#72-messagestream">A2A: Send Message Streaming</a>
 * <p>
 * This test demonstrates how to set up a Java test for the A2A client.
 * Due to Java-Kotlin interoperability limitations, this test is simplified and doesn't
 * fully replicate the functionality of the Kotlin test.
 */
class SendMessageStreamingJavaTest extends AbstractJavaTest {

    /**
     * This test demonstrates the structure of a test that would send a task streaming request.
     * <p>
     * The test demonstrates Java-Kotlin interoperability and fully replicate the functionality of the Kotlin test.
     */
    @Test
    public void shouldSendMessageStreaming() throws InterruptedException {
        String taskId = "task_12345";
        String sessionId = "session_12345";

        // 1. Configure the mock server to respond with a stream of events
        a2aServer.sendMessageStreaming().responds(responseSpec -> {

            final Stream<TaskUpdateEvent> stream = Stream.of(
                new TaskStatusUpdateEvent(
                    taskId,
                    new TaskStatus("working")
                ),

                new TaskArtifactUpdateEvent(
                    taskId,
                    new Artifact(
                        "art_1234",
                        "joke",
                        new TextPart(
                            "This"
                        ),
                        false, // append
                        false // last chunk
                    )
                ),

                new TaskArtifactUpdateEvent(
                    taskId,
                    new Artifact(
                        "art_1234",
                        "joke",
                        new TextPart(
                            "is"
                        ),
                        true, // append
                        false // last chunk
                    )
                ),

                new TaskArtifactUpdateEvent(
                    taskId,
                    new Artifact(
                        "art_1234",
                        "joke",
                        new TextPart(
                            "a"
                        ),
                        true, // append
                        false // last chunk
                    )
                ),

                new TaskArtifactUpdateEvent(
                    taskId,
                    new Artifact(
                        "art_1234",
                        "joke",
                        new TextPart(
                            "joke"
                        ),
                        true, // append
                        true // last chunk
                    )
                ),

                new TaskStatusUpdateEvent(
                    taskId,
                    TaskStatus.of("completed", new Message(Message.Role.agent, Collections.singletonList(new TextPart("Done"))), Instant.now()),
                    true // final event
                )
            );
            responseSpec.stream(stream);
        });

        // 2. Create task parameters
        final var taskSendParams = new MessageSendParams(
            new Message(
                Message.Role.user,
                List.of(new TextPart("Tell me a joke"))
            )
        );

        // 3. Call the client's sendStreamingMessage method
        final var events = new ConcurrentLinkedDeque<>();

        final var flow = sendStreamingMessageAsJavaFlow(
            client,
            taskSendParams,
            Executors.newFixedThreadPool(1)
        );

        flow.subscribe(new Flow.Subscriber<>() {

            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                logger.debug("Subscribed to the flow");
                this.subscription = subscription;
                subscription.request(1);
            }

            @Override
            public void onNext(TaskUpdateEvent item) {
                logger.debug("Received task update event: {}", item);
                events.offer(item);
                subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                logger.error("An error occurred", throwable);
                subscription.cancel();
                subscription = null;
            }

            @Override
            public void onComplete() {
                logger.info("Flow completed");
                subscription.cancel();
                subscription = null;
            }
        });

        // 4. Verify the events
        Awaitility.await()
            .untilAsserted(() -> {
                final var lastEvent = events.peekLast();
                assertThat(lastEvent).isInstanceOf(TaskStatusUpdateEvent.class);
                final var statusUpdateEvent = (TaskStatusUpdateEvent) lastEvent;
                assertThat(statusUpdateEvent.isFinal()).isTrue();
            });

        // For now, we'll just log that the test was executed
        logger.info("Test executed successfully: {}", events.stream().toList());

        final var replyText = events.stream()
            .filter(TaskArtifactUpdateEvent.class::isInstance)
            .map(it -> (TaskArtifactUpdateEvent) it)
            .map(it -> it.getArtifact().getParts().get(0))
            .filter(TextPart.class::isInstance)
            .map(it -> (TextPart) it)
            .map(TextPart::getText)
            .collect(Collectors.joining(" "));

        assertThat(replyText).isEqualTo("This is a joke");
    }
}
