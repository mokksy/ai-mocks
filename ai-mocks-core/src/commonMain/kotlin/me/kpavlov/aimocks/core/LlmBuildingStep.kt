package me.kpavlov.aimocks.core

import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer
import java.util.function.Consumer

/**
 * @param P The type of the request body.
 * @param R The type of the ChatResponseSpecification, which in turn specifies request and response types.
 */
public abstract class LlmBuildingStep<P : Any, R : ChatResponseSpecification<P, *>>(
    protected val mokksy: MokksyServer,
    protected val buildingStep: BuildingStep<P>,
) {
    public abstract infix fun responds(block: R.() -> Unit)

    /**
     * Defines the expected response from the system in response to a completion request.
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
}
