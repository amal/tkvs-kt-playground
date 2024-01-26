plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.fluxo.conf)
    alias(libs.plugins.shadow)
}

val groupName = "kt.tkvs"
group = groupName
version = libs.versions.version.toString()

setupKotlin(
    config = {
        setupCoroutines = false
        enableApiValidation = false
    },
)

dependencies {
    testImplementation(kotlin("test"))
}

tasks {
    test { useJUnitPlatform() }

    jar {
        manifest.attributes["Main-Class"] = "$groupName.MainKt"
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}
