// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

// Configuración global para todos los subproyectos
subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            // Compatibilidad con las diferentes versiones de metadatos de Kotlin
            freeCompilerArgs = freeCompilerArgs + listOf("-Xskip-metadata-version-check")
        }
    }
}