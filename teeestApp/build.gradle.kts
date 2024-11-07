plugins {
    kotlin("jvm")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":spec"))
    runtimeOnly(project(":CSVImpl"))
    runtimeOnly(project(":TXTImpl"))
    runtimeOnly(project(":PDFImpl"))
    runtimeOnly(project(":XLSXImpl"))
    //todo: za bazu dependensi
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}