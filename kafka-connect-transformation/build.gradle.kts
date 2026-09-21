plugins {
    java
    alias(libs.plugins.shadow)
}

group = "com.uuidable"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}

dependencies {
    compileOnly(libs.connect.api)
    compileOnly(libs.connect.transforms)
}

tasks.test {
    useJUnitPlatform()
}
