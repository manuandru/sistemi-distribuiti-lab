import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinWithJavaTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `java-library`
    application
    kotlin("jvm") version "1.5.31"
}

repositories {
    mavenCentral()
}

group = "sd.lab"
version = "1.0-SNAPSHOT"

dependencies {
    api("io.javalin", "javalin", "4.1.1")
    implementation("io.javalin", "javalin-openapi", "4.1.1")

    api(project(":auth-common"))
    api(project(":auth-presentation"))

    implementation("org.apache.commons", "commons-collections4", "4.2")
    implementation("org.slf4j", "slf4j-simple", "1.8.+")
}

application {
    mainClass.set("it.unibo.ds.ws.AuthService")
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "11"
    }
}
