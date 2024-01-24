plugins {
    java
}

allprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
    }

    tasks.test {
        useJUnitPlatform()
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
}
