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

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
    implementation("io.confluent", "kafka-connect-jdbc", libs.versions.kafkaConnectJdbc.get())

    integrationTestImplementation("org.apache.kafka", "connect-api", libs.versions.kafkaConnect.get())
    integrationTestCompileOnly("org.apache.kafka", "connect-runtime", libs.versions.kafkaConnect.get())

    integrationTestImplementation("org.junit.jupiter", "junit-jupiter-api", libs.versions.junit.get())
    integrationTestImplementation("org.junit.jupiter", "junit-jupiter-engine", libs.versions.junit.get())
    integrationTestImplementation("org.assertj", "assertj-core", libs.versions.assertj.get())
    integrationTestImplementation("org.mockito", "mockito-junit-jupiter", libs.versions.mockito.get())
    integrationTestImplementation("io.confluent", "kafka-connect-jdbc", libs.versions.kafkaConnectJdbc.get())
    integrationTestImplementation("org.testcontainers", "toxiproxy", libs.versions.testcontainers.get())
    integrationTestImplementation("org.testcontainers", "testcontainers", libs.versions.testcontainers.get())
    integrationTestImplementation("org.testcontainers", "kafka", libs.versions.testcontainers.get())
    integrationTestImplementation("org.testcontainers", "postgresql", libs.versions.testcontainers.get())
    integrationTestImplementation("org.apache.httpcomponents.client5", "httpclient5", libs.versions.httpClient.get())
    integrationTestImplementation("org.apache.kafka", "kafka-clients", libs.versions.kafkaConnect.get())
    integrationTestImplementation("com.fasterxml.jackson.core", "jackson-databind", libs.versions.jacksonDatabind.get())
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
