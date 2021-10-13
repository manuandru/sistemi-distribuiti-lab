dependencies {
    testImplementation("org.javatuples:javatuples:1.2")
}

tasks.create<JavaExec>("runMultiReadingThread") {
    group = "application"
    standardInput = System.`in`
    classpath = project.sourceSets.main.get().runtimeClasspath
    mainClass.set("sd.lab.concurrency.MultiReadingThreadExample")
}

tasks.create<JavaExec>("runMultiReadingService") {
    group = "application"
    standardInput = System.`in`
    classpath = project.sourceSets.main.get().runtimeClasspath
    mainClass.set("sd.lab.concurrency.MultiReadingServiceExample")
}