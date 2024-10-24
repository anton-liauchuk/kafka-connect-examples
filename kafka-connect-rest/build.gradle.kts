plugins {
    java
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
    compileOnly("javax.ws.rs", "javax.ws.rs-api", libs.versions.javaxWsRsApi.get())

    testImplementation("org.glassfish.jersey.core", "jersey-common", libs.versions.jerseyCommon.get())
    testImplementation("org.junit.jupiter", "junit-jupiter-engine", libs.versions.junit.get())
    testImplementation("org.junit.jupiter", "junit-jupiter-params", libs.versions.junit.get())
    testImplementation("org.mockito", "mockito-core", libs.versions.mockito.get())
    testImplementation("org.mockito", "mockito-junit-jupiter", libs.versions.mockito.get())
    testImplementation("org.assertj", "assertj-core", libs.versions.assertj.get())
}

configurations {
    configurations.testImplementation.get().apply {
        extendsFrom(configurations.compileOnly.get())
    }
}

tasks.test {
    useJUnitPlatform()
}
