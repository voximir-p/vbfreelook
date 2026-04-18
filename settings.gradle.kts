pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/")
		mavenCentral()
		gradlePluginPortal()
	}

	plugins {
		id("net.fabricmc.fabric-loom") version providers.gradleProperty("loom.version")
	}
}

rootProject.name = providers.gradleProperty("mod.id").get()
