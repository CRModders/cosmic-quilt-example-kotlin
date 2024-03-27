plugins {
    id("java-library")
    id("application")
    id("maven-publish")
    kotlin("jvm")
}

repositories {
    ivy {
        name = "Cosmic Reach"
        url = uri("https://cosmic-archive.netlify.app/")
        patternLayout {
            artifact("/Cosmic Reach-[revision].jar")
        }
        // This is required in Gradle 6.0+ as metadata file (ivy.xml) is mandatory
        metadataSources {
            artifact()
        }

        content {
            includeGroup("finalforeach")
        }
    }

    maven("https://jitpack.io") {
        name = "JitPack"
    }

    maven("https://maven.quiltmc.org/repository/release") {
        name = "Quilt"
    }

    maven("https://maven.fabricmc.net/") {
        name = "Fabric"
    }

    maven("https://repo.spongepowered.org/maven/") {
        name = "Sponge"
    }

    mavenCentral()
}

val cosmicreach: Configuration by configurations.creating {
    configurations.compileOnly.get().extendsFrom(this)
}

val quiltMod: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    // Cosmic Reach
    cosmicreach("finalforeach:cosmicreach:${project.properties["cosmic_reach_version"].toString()}")

    // Cosmic Quilt
    implementation("org.codeberg.CRModders:cosmic-quilt:${project.properties["cosmic_quilt_version"].toString()}")

    // FluxAPI
    //  If you don't want FluxAPI included in your project, remove this and the reference in the `gradle.properties`
//    quiltMod("com.github.CRModders:FluxAPI:${project.properties["fluxapi_version"].toString()}")

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))
}

tasks.processResources {
    // Locations of where to inject the properties
    val resourceTargets = listOf("quilt.mod.json")

    // Left item is the name in the target, right is the variable name
    val replaceProperties = mutableMapOf(
        "mod_version"     to project.version,
        "mod_group"       to project.group,
        "mod_name"        to project.name,
        "mod_id"          to project.properties["id"].toString(),
    )

    inputs.properties(replaceProperties)
    replaceProperties["project"] = project
    filesMatching(resourceTargets) {
        expand(replaceProperties)
    }
}

// Sets up all the Quilt Mods
fun getQuiltModLocations(config: Configuration): String {
    val sb = StringBuilder();
    for (obj in config.allDependencies) {
        sb.append(File.pathSeparator + config.files(obj).first())
    }
    return sb.toString()
}

var jarFile = tasks.named<Jar>("jar").flatMap { jar -> jar.archiveFile }.get().asFile
println("Mod JAR File: `$jarFile'")
val defaultArgs = listOf(
    "-Dloader.skipMcProvider=true",
    "-Dloader.gameJarPath=${cosmicreach.asPath}", // Defines path to Cosmic Reach
    "-Dloader.addMods=$jarFile${getQuiltModLocations(quiltMod)}" // Add the jar of this project
)

application {
    // As Quilt is our loader, use its main class at:
    mainClass = "org.quiltmc.loader.impl.launch.knot.KnotClient"
    applicationDefaultJvmArgs = defaultArgs
}

tasks.run.configure {
    dependsOn("jar")

    val runDir = File("run/")
    if (!runDir.exists())
        runDir.mkdirs()
    workingDir = runDir
}

java {
	withSourcesJar()
	// withJavadocJar() // If docs are included with the project, this line can be un-commented
}

sourceSets {
    main {
        java {
            srcDir("src/main/java")
        }
        kotlin {
            srcDir("src/main/kotlin")
        }
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        groupId = project.group.toString()
        artifactId = project.name

        from(components["java"])
    }

    repositories {

    }
}
