package me.kpavlov.aimocks.core

import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer
import java.util.function.Consumer

public abstract class LlmBuildingStep<R : ChatResponseSpecification<*>>(
    protected val mokksy: MokksyServer,
    protected val buildingStep: BuildingStep<*>,
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
