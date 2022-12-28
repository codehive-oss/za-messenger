plugins {
    id("messenger")
    id("application")
}

dependencies {
    implementation(project(":messenger-api"))
    implementation("org.java-websocket:Java-WebSocket:1.5.3")
}

application {
    mainClass.set("io.frghackers.messenger.client.Main")
}