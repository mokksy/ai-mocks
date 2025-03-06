plugins {
    `kotlin-dsl`
    id("com.vanniktech.maven.publish") version "0.30.0"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:7.0.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10")
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.31.0")
}
