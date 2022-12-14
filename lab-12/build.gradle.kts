plugins {
    java
}

group = "it.unibo.ds.rabbitmq"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.rabbitmq:amqp-client:5.14.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    runtimeOnly("org.slf4j:slf4j-nop:1.7.32")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

java {
    targetCompatibility = JavaVersion.VERSION_11
    sourceCompatibility = JavaVersion.VERSION_11
}

fun createRunTask(className: String, vararg propagatedProperties: String) {
    val simpleName = className.split('.').last()
    tasks.create<JavaExec>("run$simpleName") {
        group = "application"
        sourceSets.main { classpath = runtimeClasspath }
        mainClass.set("it.unibo.queues.$className")
        standardInput = System.`in`
        for (property in propagatedProperties) {
            if (project.hasProperty(property)) {
                args(project.property(property))
            }
        }
    }
}

createRunTask("base.Listener", "agent")
createRunTask("base.Sender", "agent", "message")

createRunTask("jobs.Master", "agent")
createRunTask("jobs.Worker", "agent", "master")
