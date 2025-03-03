package me.kpavlov.mokksy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.datafaker.Faker

private val faker = Faker()

@Serializable
data class TestAddress(
    val street: String,
) {
    companion object {
        fun random() =
            TestAddress(
                street = faker.address().streetAddress(),
            )
    }
}

@Serializable
data class TestPerson(
    @SerialName("person_name")
    val name: String,
    val address: List<TestAddress> = emptyList(),
) {
    companion object {
        fun random() =
            TestPerson(
                name = faker.name().fullName(),
                address =
                    listOf(
                        TestAddress.random(),
                    ),
            )
    }
}

@Serializable
data class TestOrder(
    val id: String,
    val item: String,
    val person: TestPerson,
) {
    companion object {
        fun random(person: TestPerson = TestPerson.random()) =
            TestOrder(
                id = faker.number().digits(10).toString(),
                item = faker.commerce().productName(),
                person = person,
            )
    }
}
