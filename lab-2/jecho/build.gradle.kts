plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")

    implementation("commons-cli:commons-cli:1.4")
}

application {
    mainClass.set("it.unibo.jecho.Main")
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.getByName<JavaExec>("run") {
    standardInput = System.`in`
    if (project.hasProperty("mode")) {
        when (project.property("mode").toString().toLowerCase()) {
            "uppercase", "u" -> args("-u")
            "lowercase", "l" -> args("-l")
        }
    }
}

