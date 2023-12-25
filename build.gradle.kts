plugins {
    java
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
}