package dev.mokksy.aimocks.core

import dev.mokksy.mokksy.BuildingStep
import dev.mokksy.mokksy.MokksyServer
import java.util.function.Consumer

public abstract class AbstractStreamingBuildingStep<
    P : Any,
    R : AbstractResponseSpecification<P, *>,
>(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<P>,
) : AbstractBuildingStep<P, R>(
        mokksy = mokksy,
        buildingStep = buildingStep,
    ) {
    public abstract infix fun respondsStream(block: suspend R.() -> Unit)

    public override fun responds(block: suspend R.() -> Unit) {
        respondsStream(block)
    }

    public open infix fun respondsStream(consumer: Consumer<R>) {
        respondsStream { consumer.accept(this) }
    }
}
