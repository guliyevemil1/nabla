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
}

dependencies {
//    implementation(kotlin("stdlib-js"))
//
//    testImplementation(kotlin("test"))
}
