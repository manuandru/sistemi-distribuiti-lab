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
//    implementation("org.javatuples", "javatuples", "1.2")
    testImplementation("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_10
    targetCompatibility = JavaVersion.VERSION_1_10
}