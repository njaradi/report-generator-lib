plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.librepdf:openpdf:1.3.29")
    implementation(project(":spec"))
    testImplementation(kotlin("test"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"]) // If you're using the 'java' or 'kotlin' plugin

            groupId = "org.example"
            artifactId = "PDFImpl"
            version = "1.0.0"
        }
    }
}
tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}