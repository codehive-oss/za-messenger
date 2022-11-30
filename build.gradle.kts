plugins {
    id("messenger")
}

tasks {
    register("runClientAndServer") {
        dependsOn(":messenger-server:run")
        dependsOn(":messenger-client:run")
    }
}