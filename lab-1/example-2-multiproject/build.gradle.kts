plugins {
    java
}

subprojects {
    apply(plugin = "java")

    repositories {
        // Use Maven Central for resolving dependencies.
        mavenCentral()
    }

    dependencies {
        // Use JUnit Jupiter for testing.
        testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
    }

    tasks.test {
        // Use JUnit Platform for unit tests.
        useJUnitPlatform()
    }
}
