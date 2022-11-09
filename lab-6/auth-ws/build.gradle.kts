//import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `java-library`
    application
}

repositories {
    mavenCentral()
}

group = "sd.lab"
version = "1.0-SNAPSHOT"

dependencies {
    api("io.javalin:javalin:5.1.3")

    api(project(":auth-common"))
    api(project(":auth-presentation"))

    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("org.slf4j:slf4j-simple:2.0.3")

    annotationProcessor("io.javalin.community.openapi:openapi-annotation-processor:5.1.3")

    implementation("io.javalin.community.openapi:javalin-openapi-plugin:5.1.3") // for /openapi route with JSON scheme
    implementation("io.javalin.community.openapi:javalin-swagger-plugin:5.1.3") // for Swagger UI
}

application {
    mainClass.set("it.unibo.ds.ws.AuthService")
}
