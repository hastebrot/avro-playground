import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val gradleWrapperVersion: String by project
val kotlinVersion: String by project
val junitVersion: String by project

plugins {
    val kotlinPluginVersion = "1.5.21"
    val avroPluginVersion = "1.2.1"

    kotlin("jvm") version kotlinPluginVersion
    kotlin("plugin.serialization") version kotlinPluginVersion
    id("com.github.davidmc24.gradle.plugin.avro") version avroPluginVersion
}

repositories {
    mavenCentral()
}

dependencies {
    val avroVersion = "1.10.2"
    val avro4kVersion = "1.3.0"
    val serializationVersion = "1.2.2"

    implementation(kotlin("stdlib", kotlinVersion))
    implementation(kotlin("stdlib-jdk7", kotlinVersion))
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation(kotlin("reflect", kotlinVersion))

    implementation("org.apache.avro:avro:$avroVersion")
    implementation("org.apache.avro:avro-tools:$avroVersion")
    implementation("com.github.avro-kotlin.avro4k:avro4k-core:$avro4kVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
}

dependencies {
    testImplementation(kotlin("test", kotlinVersion))
    testImplementation(kotlin("test-junit5", kotlinVersion))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

avro {
    isGettersReturnOptional.set(false)
    fieldVisibility.set("private")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    withType<Wrapper> {
        gradleVersion = gradleWrapperVersion
        distributionType = Wrapper.DistributionType.ALL
    }

    register<JavaExec>("generateAvroSchemas") {
        group = "source generation"
        classpath = sourceSets.main.get().runtimeClasspath
        mainClass.set("avro.kotlin.GenerateAvroSchemasKt")
    }
}
