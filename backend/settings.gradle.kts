pluginManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/snapshot") } // SNAPSHOT 저장소 추가
    }
    plugins {
        id("org.jetbrains.kotlin.jvm") version "2.1.10"
    }
}

rootProject.name = "backend"