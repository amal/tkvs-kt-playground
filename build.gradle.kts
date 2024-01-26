plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.fluxo.conf)
}

group = "kt.tkvs"
version = libs.versions.version.toString()

setupKotlin(
    config = {
        enableApiValidation = false
    }
)

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
