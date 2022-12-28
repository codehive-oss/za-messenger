plugins {
    id("messenger")
    id("application")
}

dependencies {
    implementation(project(":messenger-api"))
    implementation("de.mkammerer:argon2-jvm:2.11")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.xerial:sqlite-jdbc:3.40.0.0")
    implementation("org.java-websocket:Java-WebSocket:1.5.3")
}

application {
    mainClass.set("io.frghackers.messenger.server.Main")
}