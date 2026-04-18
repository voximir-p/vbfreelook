plugins {
	// `maven-publish`
	id("net.fabricmc.fabric-loom")
	id("me.fallenbreath.yamlang") version "1.5.0"
}

fun getProperty(name: String) = providers.gradleProperty(name).get()

val minecraftVersion = getProperty("minecraft.version")
val loaderVersion = getProperty("loader.version")
val javaVersion = getProperty("java.version").toInt()

val modVersion = getProperty("mod.version")
val modGroup = getProperty("mod.group")
val modId = getProperty("mod.id")

val fabricApiVersion = getProperty("fabric-api.version")
val modmenuVersion = getProperty("modmenu.version")
val yaclVersion = getProperty("yacl.version")

version = modVersion
group = modGroup

repositories {
	maven("https://maven.terraformersmc.com/releases")
	exclusiveContent {
		forRepository { maven("https://api.modrinth.com/maven") }
		filter { includeGroup("maven.modrinth") }
	}
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraftVersion")

	implementation("net.fabricmc:fabric-loader:$loaderVersion")
	implementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")

	implementation("com.terraformersmc:modmenu:$modmenuVersion")
	implementation("maven.modrinth:yacl:$yaclVersion")

	// Only for config reference
	runtimeOnly("maven.modrinth:do-a-barrel-roll:3.8.4+26.1-fabric")
	runtimeOnly("maven.modrinth:zoomify:2.16.0+26.1")
	runtimeOnly("maven.modrinth:freelook:1.3.0")

	// Required for the reference mods
	runtimeOnly("maven.modrinth:fabric-language-kotlin:1.13.10+kotlin.2.3.20")
	runtimeOnly("maven.modrinth:cicada:0.15.1+26.1")
}

tasks.named<Copy>("processResources") {
	val stringProperties = project.properties
		.filterValues { it is String }
		.mapValues { it.value.toString() }

	// Groovy template expansion treats dots as property traversal (a.b.c), so
	// expose underscore aliases for dotted gradle property names.
	val templateProperties = buildMap {
		putAll(stringProperties)
		stringProperties
			.filterKeys { '.' in it }
			.forEach { (key, value) -> put(key.replace('.', '_'), value) }
	}

	inputs.properties(templateProperties)

	filesMatching(listOf("*.mixins.json", "*.mod.json")) {
		expand(templateProperties)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release.set(javaVersion)
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(javaVersion))
	}

	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	// withSourcesJar()

	sourceCompatibility = JavaVersion.toVersion(javaVersion)
	targetCompatibility = JavaVersion.toVersion(javaVersion)
}

yamlang {
	targetSourceSets = listOf(sourceSets.main.get())
	inputDir = "assets/$modId/lang"
}

/*
// configure the maven publication
publishing {
	publications {
		register<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}
*/
