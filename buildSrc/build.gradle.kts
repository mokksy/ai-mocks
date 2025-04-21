plugins {
    `kotlin-dsl`
    id("com.vanniktech.maven.publish") version "0.31.0"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:7.0.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.20")
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.31.0")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:2.0.0")
}
