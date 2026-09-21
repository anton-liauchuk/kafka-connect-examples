plugins {
    java
    alias(libs.plugins.shadow)
}

group = "com.uuidable"
version = "1.0-SNAPSHOT"

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
