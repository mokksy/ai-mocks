plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(libs.dokka.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.gradle.maven.publish.plugin)
    implementation("com.diffplug.spotless:spotless-plugin-gradle:8.2.1")
    implementation("com.gradleup.shadow:shadow-gradle-plugin:9.3.1")
}
