plugins {
    id("messenger")
}

allprojects {
    repositories {
        mavenCentral()
    }
}

tasks {
    register("runClientAndServer") {
        dependsOn(":messenger-server:run")
        dependsOn(":messenger-client:run")
    }
}