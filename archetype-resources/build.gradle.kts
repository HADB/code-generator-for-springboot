group = "${group_id}"

version = "${version}"

description = "${description}"

plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.noarg") version "2.3.0"
    kotlin("plugin.spring") version "2.3.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.springframework.boot") version "4.0.1"
    id("org.flywaydb.flyway") version "12.0.0"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    maven {
        url = uri("https://maven.aliyun.com/repository/central")
        name = "Aliyun Maven Central Repository"
    }
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-jackson2")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Other Dependencies
    implementation("com.google.guava:guava:33.5.0-jre")
    implementation("commons-codec:commons-codec")
    implementation("org.apache.commons:commons-lang3")
    implementation("org.apache.commons:commons-pool2")
    implementation("org.apache.httpcomponents.client5:httpclient5")
    implementation("org.apache.poi:poi-ooxml:5.4.0")
    implementation("org.flywaydb:flyway-core:12.0.0")
    implementation("org.flywaydb:flyway-mysql:12.0.0")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:4.0.1")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
    implementation("tools.jackson.module:jackson-module-kotlin")

    // Runtime Dependencies
    runtimeOnly("com.mysql:mysql-connector-j")
}

noArg {
    annotation("${package_name}.annotations.NoArg")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.bootJar {
    archiveFileName.set("app.jar")
}
