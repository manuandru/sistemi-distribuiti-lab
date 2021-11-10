plugins {
    java
    `java-library`
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
