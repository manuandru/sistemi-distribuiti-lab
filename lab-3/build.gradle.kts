plugins {
    java
}

group = "sd.lab"
version = "1.0-SNAPSHOT"

subprojects {
    apply<JavaPlugin>()

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation("org.junit.jupiter", "junit-jupiter", "5.7.2")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks.test {
        useJUnitPlatform()
    }
}
