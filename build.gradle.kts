import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import kotlin.system.exitProcess

val ktor_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.8.0"
    application
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.22"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "moe.fuqiuluo"
version = "1.1.9"

repositories {
    mavenCentral()
    maven("https://kotlin.bintray.com/ktor")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.3.3")
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    implementation(fileTree(mapOf(
        "dir" to "libs",
        "include" to listOf("*.jar")
    )))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}

distributions {
    main {
        contents {
            from(".") {
                include("txlib/**")
            }
        }
    }
}


tasks {
    register("generateProjectFile") {
        val dir = File("src/main/java/project").apply { mkdirs() }
        dir.resolve("BuildConfig.java").also {
            if (!it.exists()) it.createNewFile()
        }.writer().use {
            it.write("public class BuildConfig {")
            it.write("    public static String version = \"${project.version}\";")
            it.write("}")
        }
    }
    named("prepareKotlinBuildScriptModel").configure {
        dependsOn("generateProjectFile")
    }
    named("processResources") {
        dependsOn("generateProjectFile")
    }
}