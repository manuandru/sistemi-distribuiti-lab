plugins {
    application
}

application {
    // Define the main class for the application.
    mainClass.set("it.unibo.ds.lab.sockets.server.EchoServer")
}

tasks.getByName<JavaExec>("run") {
    standardInput = System.`in`
    if (project.hasProperty("port")) {
        args(project.property("port"))
    }
}

dependencies {
    testImplementation(project(":test"))
}
