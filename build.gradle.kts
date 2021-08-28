import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val gradleWrapperVersion: String by project // 7.2 = 17 Aug 2021
val kotlinVersion: String by project //  1.5.30 = 24 Aug 2021
val junitVersion: String by project // 5.7.2 = 15 May 2021

plugins {
    val kotlinPluginVersion = "1.5.30" // 24 Aug 2021
    val avroPluginVersion = "1.2.1" // 23 Jul 2021

    kotlin("jvm") version kotlinPluginVersion
    kotlin("plugin.serialization") version kotlinPluginVersion
    id("com.github.davidmc24.gradle.plugin.avro") version avroPluginVersion
}

repositories {
    mavenCentral()
}

dependencies {
    val avroVersion = "1.10.2" // 17 Mar 2021
    val avro4kVersion = "1.3.0" // 1 Jul 2021
    val log4jVersion = "2.14.1" // 12 Mar 2021
    val serializationVersion = "1.2.2" // 8 Jul 2021

    implementation(kotlin("stdlib", kotlinVersion))
    implementation(kotlin("stdlib-jdk7", kotlinVersion))
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation(kotlin("reflect", kotlinVersion))

    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")

    implementation("org.apache.avro:avro:$avroVersion")
    implementation("org.apache.avro:avro-compiler:$avroVersion")
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
