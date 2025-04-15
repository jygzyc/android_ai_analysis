plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "me.yvesz"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.convention(JavaLanguageVersion.of(17))
	}
}

repositories {
	mavenCentral()
	google()
}

extra["springAiVersion"] = "1.0.0-M6"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("com.google.code.gson:gson:2.10.1")

	implementation("io.github.skylot:jadx-core:1.5.1")
	implementation("io.github.skylot:jadx-cli:1.5.1")
	implementation("io.github.skylot:jadx-rename-mappings:1.5.1")
	implementation("io.github.skylot:jadx-dex-input:1.5.1")
	implementation("io.github.skylot:jadx-smali-input:1.5.1")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
