plugins {
    application
}

dependencies {
    implementation(project(":lib"))
}

application {
    mainClass.set("it.unibo.ds.multiproject.App")
}
