// Top-level build file where you can add configuration options common to all sub-projects/modules.


plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false  // Actualizado a 1.9.21
    id("com.google.devtools.ksp") version "1.9.21-1.0.15" apply false  // Versión compatible con Kotlin 1.9.21
    id("com.google.dagger.hilt.android") version "2.50" apply false  // Actualizado a 2.50
    id("com.google.gms.google-services") version "4.4.1" apply false
}

