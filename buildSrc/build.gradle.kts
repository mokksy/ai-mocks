plugins {
    `kotlin-dsl`
    id("com.vanniktech.maven.publish") version "0.34.0"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:7.1.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.21")
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.34.0")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:2.0.0")
}
