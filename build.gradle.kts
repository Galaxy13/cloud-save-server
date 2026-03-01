import com.diffplug.spotless.java.GoogleJavaFormatStep
import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("java")
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.github.node-gradle.node") version "7.0.2"
    id("com.diffplug.spotless") version "7.2.1"
//    checkstyle
}

//checkstyle {
//    config = resources.text.fromUri("https://raw.githubusercontent.com/OtusTeam/Spring/master/checkstyle.xml")
//}

spotless {
    java {
        googleJavaFormat(GoogleJavaFormatStep.defaultVersion()).skipJavadocFormatting().aosp()
        targetExclude("build/**", "/backend/build/generated/**")
    }
}

group = "com.galaxy13"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
//    implementation("com.jlefebure:spring-boot-starter-minio")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.postgresql:postgresql")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // OpenAPI/Swagger documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // MinIO S3 Client
    implementation("io.minio:minio:8.6.0")

    // JWT for API authentication
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

node {
    version.set("20.19.0")
    npmVersion.set("10.2.4")
    download.set(true)
}

//val adminDir = file("src/ui/admin-panel")
//val adminDist = file("src/ui/admin-panel/dist")
//val adminTarget = file("src/main/resources/static/admin")
//
//val adminNpmInstall = tasks.register<NpmTask>("adminNpmInstall") {
//    dependsOn("npmInstall")
//    workingDir.set(adminDir)
//    args.set(listOf("ci"))
//}
//
//val adminBuild = tasks.register<NpmTask>("adminBuild") {
//    dependsOn(adminNpmInstall)
//    workingDir.set(adminDir)
//    args.set(listOf("run", "build"))
//    inputs.dir(adminDist)
//    outputs.dir(adminDist)
//}
//
//val adminCopy = tasks.register<Copy>("adminCopy") {
//    dependsOn(adminBuild)
//    from(adminDist)
//    into(adminTarget)
//}
//
//tasks.named("processResources") {
//    dependsOn(adminCopy)
//}

tasks.test {
    useJUnitPlatform()
}