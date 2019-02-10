import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.3.21"
    application
    id("com.github.johnrengelman.shadow") version "4.0.4"
}

group = "pt.br.mok"
version = "1.0.0"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-server-netty:1.1.2")
    implementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.11.1")
    implementation("com.lmax:disruptor:3.4.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.+")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.+")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val shadowJar: ShadowJar by tasks

shadowJar.apply {
    classifier = "all"
}

application {
    mainClassName = "pt.br.mok.ServerKt"
}