plugins {
    java
    distribution
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

sourceSets {
    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val integrationTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val integrationTestRuntimeOnly: Configuration by configurations.getting
configurations["integrationTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

val integrationTestCompileOnly: Configuration by configurations.getting
configurations["integrationTestCompileOnly"].extendsFrom(configurations.runtimeOnly.get())

val integrationTest = task<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    shouldRunAfter("test")

    useJUnitPlatform()

    testLogging {
        events("passed")
    }

    dependsOn(tasks.installDist)
}

tasks.check { dependsOn(integrationTest) }

dependencies {
    implementation(project(":kafka-connect-rest"))
    implementation(project(":kafka-connect-transformation"))
    implementation(libs.kafka.connect.jdbc)

    integrationTestImplementation(libs.connect.api)
    integrationTestCompileOnly(libs.connect.runtime)

    integrationTestImplementation(libs.junit.jupiter.api)
    integrationTestImplementation(libs.junit.jupiter.engine)
    integrationTestRuntimeOnly(libs.junit.platform.launcher)
    integrationTestImplementation(libs.assertj.core)
    integrationTestImplementation(libs.mockito.junit.jupiter)
    integrationTestImplementation(libs.kafka.connect.jdbc)
    integrationTestImplementation(libs.testcontainers)
    integrationTestImplementation(libs.testcontainers.kafka)
    integrationTestImplementation(libs.testcontainers.postgresql)
    integrationTestImplementation(libs.httpclient5)
    integrationTestImplementation(libs.kafka.clients)
    integrationTestImplementation(libs.jackson.databind)
}

distributions {
    main {
        contents {
            into("plugins/") {
                from(configurations.runtimeClasspath)
            }
        }
    }
}
