plugins {
    id("java-library")
    id("application")
    id("maven-publish")
    kotlin("jvm")
}

repositories {
    ivy {
        name = "Cosmic Reach"
        url = uri("https://github.com/CRModders/CosmicArchive/raw/main/versions/")
        patternLayout {
            artifact("[classifier]/Cosmic Reach-[revision].jar")
        }
        // This is required in Gradle 6.0+ as metadata file (ivy.xml) is mandatory
        metadataSources {
            artifact()
        }

        content {
            includeModule("finalforeach", "cosmicreach")
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


    /* // CRM repos, you may or may not want it (you won't need it as CRM stuff is also on jitpack).
    maven("https://maven.crmodders.dev/releases") {
        name = "crmReleases"
    }
     */


    mavenCentral()
}


// Config to provide the Cosmic Reach project
val cosmicreach: Configuration by configurations.creating {
    configurations.compileOnly.get().extendsFrom(this)
}
// Allows to include something without it being in the maven (recommended to be used when including mods)
val internal: Configuration by configurations.creating {
    isVisible = false
    isCanBeConsumed = false
    isCanBeResolved = false
}
configurations["compileClasspath"].extendsFrom(internal)
configurations["runtimeClasspath"].extendsFrom(internal)
configurations["testCompileClasspath"].extendsFrom(internal)
configurations["testRuntimeClasspath"].extendsFrom(internal)


dependencies {
    // Cosmic Reach
    cosmicreach("finalforeach:cosmicreach:${project.properties["cosmic_reach_version"].toString()}:pre-alpha")
    // Cosmic Quilt
    internal("org.codeberg.CRModders:cosmic-quilt:${project.properties["cosmic_quilt_version"].toString()}")

    // Modmenu
//    internal("org.codeberg.CRModders:modmenu:${project.properties["modmenu_version"].toString()}")

    // Kotlin
    internal("org.codeberg.CRModders:kosmic:${project.properties["kosmic_version"].toString()}")
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


var jarFile = tasks.named<Jar>("jar").flatMap { jar -> jar.archiveFile }.get().asFile
val defaultArgs = listOf(
        "-Dloader.development=true", // Allows stuff to be found through the classpath
        "-Dloader.gameJarPath=${cosmicreach.asPath}", // Defines path to Cosmic Reach
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
