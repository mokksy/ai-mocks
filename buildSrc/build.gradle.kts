plugins {
    `kotlin-dsl`
    id("com.vanniktech.maven.publish") version "0.36.0"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:8.2.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.21")
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.35.0")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:2.1.0")
    implementation("com.gradleup.shadow:shadow-gradle-plugin:9.3.1")
}
