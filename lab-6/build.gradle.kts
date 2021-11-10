plugins {
    java
}

allprojects {
    repositories {
        mavenCentral()
    }

    group = "it.unibo.ds.ws"
    version = "0.1.0"
}

subprojects {
    apply(plugin = "java")

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    }

    tasks.test {
        useJUnitPlatform()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
