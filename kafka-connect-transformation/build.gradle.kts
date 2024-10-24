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
    compileOnly("org.apache.kafka", "connect-api", libs.versions.kafkaConnect.get())
    compileOnly("org.apache.kafka", "connect-transforms", libs.versions.kafkaConnect.get())
}

tasks.test {
    useJUnitPlatform()
}
