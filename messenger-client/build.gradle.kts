plugins {
    id("messenger")
    id("application")
}

dependencies {
    implementation(project(":messenger-api"))
}

application {
    mainClass.set("io.frghackers.messenger.client.Main")
}