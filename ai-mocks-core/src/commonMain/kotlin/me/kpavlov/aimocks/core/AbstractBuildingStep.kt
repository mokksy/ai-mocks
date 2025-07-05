package me.kpavlov.aimocks.core

import io.ktor.util.logging.Logger
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer
import me.kpavlov.mokksy.response.ResponseDefinitionBuilder
import java.util.function.Consumer
import kotlin.reflect.KClass

/**
 * Defines a building step for constructing a mock response based on specific request scenarios
 * within a mock server for large language model interactions.
 *
 * This class provides methods to specify both expected responses and error responses
 * for completions requests. It serves as a framework for configuring the behavior
 * of a mock server's response mechanism.
 *
 * @param P The type of the request body for the interaction.
 * @param R A type that extends [ResponseSpecification], representing configuration
 *          for the expected response specification.
 * @property mokksy A reference to the mock server instance.
 * @property buildingStep A reference to the internally managed building step
 *                        for configuring mock response behavior.
 */
public abstract class AbstractBuildingStep<P : Any, R : ResponseSpecification<P, *>>(
    protected val mokksy: MokksyServer,
    protected val buildingStep: BuildingStep<P>,
) {
    protected val logger: Logger = mokksy.logger

    public abstract infix fun responds(block: R.() -> Unit)

    /**
     * Defines the expected response from the system in response to a completions request.
     *
     * This method allows specifying the response using a Consumer-like approach for Java interop.
     *
     * @param block a Consumer that configures the resulting response by applying
     *              specifications to an instance of the response object.
     */
    public open infix fun responds(block: Consumer<R>) {
        responds {
            block.accept(this)
        }
    }

    /**
     * Specifies an error response for a given request scenario.
     * This allows defining the characteristics of an erroneous response,
     * such as the HTTP status, response body, headers, delay, and more.
     *
     * @param T The type of the response body for the error response.
     * @param block A lambda function applied to a [ResponseDefinitionBuilder],
     * used to configure the error response properties
     * such as the body, status, headers, and other attributes.
     */
    public open infix fun <T : Any> respondsError(
        block: ResponseDefinitionBuilder<P, T>.() -> Unit,
    ) {
        buildingStep.respondsWith {
            block(this)
        }
    }

    public open fun <T : Any> respondsError(
        responseType: KClass<T>,
        block: ResponseDefinitionBuilder<P, T>.() -> Unit,
    ) {
        respondsError<T>(block)
    }

    /**
     * Specifies an erroneous response for a completions request.
     *
     * This method enables defining the error response using a Consumer-like approach for interoperability with Java.
     *
     * @param block a Consumer that configures the error response by applying
     * specifications to an instance of the [ResponseDefinitionBuilder].
     * The [ResponseDefinitionBuilder] allows setting attributes such as
     * the HTTP status, response body, headers, and more.
     *
     * @param R The type of the response body for the error response.
     */
    public open infix fun <R : Any> respondsError(
        block: Consumer<ResponseDefinitionBuilder<P, R>>,
    ) {
        buildingStep.respondsWith {
            block.accept(this)
        }
    }
}

public abstract class AbstractStreamingBuildingStep<P : Any, R : ResponseSpecification<P, *>>(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<P>,
) : AbstractBuildingStep<P, R>(
    mokksy = mokksy,
    buildingStep = buildingStep,
) {

    public abstract infix fun respondsStream(block: R.() -> Unit)

    public override fun responds(block: R.() -> Unit) {
        respondsStream(block)
    }

    public open infix fun respondsStream(consumer: Consumer<R>) {
        respondsStream { consumer.accept(this) }
    }
}
