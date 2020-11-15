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
    api("org.apache.commons", "commons-collections4", "4.2")
    implementation("io.javalin", "javalin", "3.11.0")
    implementation("com.google.code.gson", "gson", "2.8.6")
    runtimeOnly("org.slf4j", "slf4j-simple", "1.8.+")
    testImplementation("junit", "junit", "4.12")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}