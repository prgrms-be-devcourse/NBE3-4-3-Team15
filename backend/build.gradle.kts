plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("io.freefair.lombok") version "8.6"
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
    // ✅ [Spring Boot Core]
    implementation("org.springframework.boot:spring-boot-starter-web") // 웹 애플리케이션
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") // JPA (데이터베이스 ORM)
    implementation("org.springframework.boot:spring-boot-starter-security") // Spring Security (인증, 인가)
    implementation("org.springframework.boot:spring-boot-starter-validation") // 유효성 검사 (Validation)

    // ✅ [OAuth2 / JWT (보안 및 인증)]
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client") // OAuth2 클라이언트
    implementation("io.jsonwebtoken:jjwt-api:0.11.5") // JWT API
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5") // JWT 구현체
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5") // JWT JSON 변환

    // ✅ [Database (H2 & MySQL)]
    runtimeOnly("com.h2database:h2") // H2 인메모리 DB
    runtimeOnly("com.mysql:mysql-connector-j") // MySQL 드라이버

    // ✅ [Swagger (API 문서화)]
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.1") // Springdoc OpenAPI UI

    // ✅ [Lombok (코드 간결화)]
    compileOnly("org.projectlombok:lombok") // Lombok (컴파일 시 코드 자동 생성)
    annotationProcessor("org.projectlombok:lombok") // Lombok annotation 처리기

    // ✅ [Spring Boot DevTools]
    developmentOnly("org.springframework.boot:spring-boot-devtools") // DevTools (자동 재시작)

    // ✅ [Test 관련]
    testAnnotationProcessor ("org.projectlombok:lombok:1.18.30") // Lombok 어노테이션 처리기 (테스트 코드에서 Lombok 사용 시)
    testImplementation("org.springframework.boot:spring-boot-starter-test") // 테스트 관련 (Junit 포함)
    testImplementation("org.springframework.security:spring-security-test") // Spring Security 테스트
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0") // JUnit 최신 버전
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0") // JUnit 엔진 (런타임)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0") // JUnit 플랫폼 런처

    // ✅ [Redis 관련 (Spring Data Redis)]
    implementation("org.springframework.boot:spring-boot-starter-data-redis") // Spring Data Redis
    implementation("org.redisson:redisson-spring-boot-starter:3.18.0") // Redisson (Redis 클라이언트)

    // ✅ [Kotlin 관련]
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin") // Jackson Kotlin 모듈
    implementation("org.jetbrains.kotlin:kotlin-reflect") // Kotlin 리플렉션 라이브러리

    // ✅ [비동기 및 크롤링 관련]
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") // 코루틴
    implementation("org.jsoup:jsoup:1.18.3") // HTML 파싱
    implementation("com.microsoft.playwright:playwright:1.42.0") // Playwright (브라우저 자동화)
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
