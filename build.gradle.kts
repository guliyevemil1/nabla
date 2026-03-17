plugins {
    kotlin("multiplatform") version "2.3.20"
}

group = "org.guliyevemil1"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    js {
        browser {
        }
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.9.1")
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

dependencies {
//    implementation(kotlin("stdlib-js"))
//
//    testImplementation(kotlin("test"))
}
