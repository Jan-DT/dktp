plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.7"
}

group = "nl.jandt.dktp"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("ch.qos.logback:logback-core:1.5.4")
    implementation("ch.qos.logback:logback-classic:1.5.4")
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("net.minestom:minestom-snapshots:461c56e749")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("dev.hollowcube:polar:1.11.1")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "nl.jandt.dktp.Game"
    }
}
