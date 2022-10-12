import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

application {
	// defines the main class for the application
	mainClass.set("pb/ghcodecoverage.GhCodeCoverageApplicationKt")
	version = "1.0-SNAPSHOT"
}

plugins {
	id("org.springframework.boot") version "2.7.4"
	id("io.spring.dependency-management") version "1.0.14.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	kotlin("plugin.jpa") version "1.6.21"
	application
}

group = "pb"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.7.4")
	implementation("org.springframework.boot:spring-boot-starter-web:2.7.4")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.4")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.squareup.okhttp3:okhttp:4.10.0")
	implementation("io.github.cdimascio:dotenv-kotlin:6.3.1")
	runtimeOnly("com.h2database:h2:2.1.214")
	testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.4") {
		exclude(module = "mockito-core")
	}
	testImplementation("com.ninja-squad:springmockk:3.1.1")
	testImplementation("org.assertj:assertj-core:3.23.1")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
