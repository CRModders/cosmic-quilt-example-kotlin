plugins {
    id("java-library")
    id("application")
    id("maven-publish")
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


val cosmicreach = configurations.create("cosmicreach")


dependencies {
    // Cosmic Reach jar
    cosmicreach("finalforeach:cosmicreach:${project.properties["cosmic_reach_version"].toString()}") // Allows it to be used later in the gradle configuration
    implementation("finalforeach:cosmicreach:${project.properties["cosmic_reach_version"].toString()}") // Allows it to be referenced in the code

    // Cosmic Quilt
    implementation("org.codeberg.CRModders:cosmic-quilt:${project.properties["cosmic_quilt_version"].toString()}")

    //  The dependencies below are part of Cosmic Quilt
    // Quilt Loader
    implementation("org.quiltmc:quilt-loader:${project.properties["quilt_loader_version"].toString()}")
    implementation("org.quiltmc:quilt-json5:${project.properties["quilt_json_version"].toString()}")
    implementation("org.quiltmc:tiny-remapper:${project.properties["quilt_remapper_version"].toString()}")
    implementation("org.quiltmc:quilt-config:${project.properties["quilt_config_version"].toString()}")
    implementation("net.fabricmc:access-widener:${project.properties["fabric_accesswidener_version"].toString()}")
    // Slf4j
    implementation("org.slf4j:slf4j-api:${project.properties["slf4j_version"].toString()}")
    implementation("org.slf4j:slf4j-jdk14:${project.properties["slf4j_version"].toString()}")
    implementation("uk.org.lidalia:sysout-over-slf4j:${project.properties["slf4j_sysout_helper_version"].toString()}")
    // Mixins
    implementation("org.spongepowered:mixin:${project.properties["mixin_version"].toString()}")
    implementation("io.github.llamalad7:mixinextras-fabric:${project.properties["mixinextras_version"].toString()}") // Note: Despite it saying "fabric", its implementation is also for quilt
    // Extra libraries
    implementation("com.google.guava:guava:${project.properties["guava_version"].toString()}")
    implementation("com.google.code.gson:gson:${project.properties["gson_version"].toString()}")
    implementation("org.ow2.asm:asm:${project.properties["asm_version"].toString()}")
    implementation("org.ow2.asm:asm-util:${project.properties["asm_version"].toString()}")
    implementation("org.ow2.asm:asm-tree:${project.properties["asm_version"].toString()}")
    implementation("org.ow2.asm:asm-analysis:${project.properties["asm_version"].toString()}")
    implementation("org.ow2.asm:asm-commons:${project.properties["asm_version"].toString()}")
}

tasks.processResources {
    val resourceTargets = listOf( // Locations of where to inject the properties
        "quilt.mod.json"
    )

    // Left item is the name in the target, right is the varuable name
    val replaceProperties = mapOf(
        "mod_version"     to project.version,
        "mod_group"       to project.group,
        "mod_name"        to project.name,
        "mod_id"          to project.properties["id"].toString(),
    )


    inputs.properties(replaceProperties)
//    replaceProperties.add("project", project)
    filesMatching(resourceTargets) {
        expand(replaceProperties)
    }
}

var jarFile = tasks.named<Jar>("jar").flatMap { jar -> jar.archiveFile }.get().asFile
val defaultArgs = listOf(
    "-Dloader.skipMcProvider=true",
    "-Dloader.gameJarPath=${cosmicreach.asPath}", // Defines path to Cosmic Reach
    "-Dloader.addMods=$jarFile" // Add the jar of this project
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

    // Sets the Java version
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
