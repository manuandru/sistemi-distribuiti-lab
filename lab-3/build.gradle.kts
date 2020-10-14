plugins {
    java
}

repositories {
    mavenCentral()
}

group = "sd.lab"
version = "1.0-SNAPSHOT"

dependencies {
    testImplementation("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_10
    targetCompatibility = JavaVersion.VERSION_1_10
}
