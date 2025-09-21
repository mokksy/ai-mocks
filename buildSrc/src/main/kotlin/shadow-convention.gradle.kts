import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar

plugins {
	`maven-publish`
	id("org.gradle.base")
	id("com.gradleup.shadow") // https://gradleup.com/shadow
}

tasks.shadowJar {

	minimize {
		exclude(dependency("kotlin:.*:.*"))
		exclude(dependency("org.jetbrains.kotlin:.*:.*"))
		exclude(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-.*:.*"))
	}

	dependencies {
		relocate("io.ktor", "dev.mokksy.relocated.io.ktor")
		relocate("kotlinx", "dev.mokksy.relocated.kotlinx")
	}
}

tasks.assemble {
	dependsOn(tasks.shadowJar)
}
