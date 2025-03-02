plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
}

group = "jpabook"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // ✅ Spring Boot 기본 스타터
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    // ✅ Kotlin 관련 라이브러리
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // ✅ DB 관련 라이브러리
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2")
    implementation("redis.clients:jedis:4.3.1")

    // ✅ API 문서화 (Swagger)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.1")

    // ✅ ModelMapper (객체 변환)
    implementation("org.modelmapper:modelmapper:3.1.1")

    // ✅ JWT 인증 관련
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // ✅ 비동기 및 크롤링 관련
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") // 코루틴
    implementation("org.jsoup:jsoup:1.18.3") // HTML 파싱
    implementation("com.microsoft.playwright:playwright:1.42.0") // Playwright (브라우저 자동화)

    // ✅ 개발 편의 기능
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // ✅ 테스트 관련 라이브러리
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0") // JUnit 최신 버전
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0") // JUnit 플랫폼 런처 추가
}


kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
