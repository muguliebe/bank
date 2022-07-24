import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8
val appName = "bank"

repositories {
    mavenCentral()
}

dependencies {
    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // spring
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework:spring-tx")

    // database: embedded postgresql
    runtimeOnly("org.postgresql:postgresql")
    implementation("com.opentable.components:otj-pg-embedded:1.0.1")
    implementation("org.flywaydb:flyway-core:8.5.12")

    // mybatis
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:2.2.2")
    implementation("org.mybatis:mybatis:3.5.10")
    implementation("org.mybatis:mybatis-spring:2.0.7")
    implementation("org.mybatis:mybatis-typehandlers-jsr310:1.0.2")
    implementation("com.google.guava:guava:31.1-jre")

    // etc
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-jackson:2.3.1")
    implementation("commons-io:commons-io:2.11.0")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("io.springfox:springfox-swagger2:2.10.5")
    implementation("io.springfox:springfox-swagger-ui:2.10.5")



    // develop
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.apache.httpcomponents:httpclient:4.5.9")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }
    withType<Test> {
        useJUnitPlatform()
    }
    bootJar {
        manifest {
            attributes("Multi-Release" to true)
        }

        archiveBaseName.set(appName)
        archiveVersion.set(version!!)

        if (project.hasProperty("archiveName")) {
            archiveFileName.set(project.properties["archiveName"] as String)
        }
    }
}
