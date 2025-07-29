plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.0.10"
    id("org.jetbrains.intellij.platform") version "2.7.0"
}

group = "com.github.nizienko"
version = "1.1.4"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2025.1")
        instrumentationTools()
        pluginVerifier()
    }
}

intellijPlatform {
    pluginVerification {
        ides {
            recommended()
        }
    }
    pluginConfiguration {
        ideaVersion {
            sinceBuild.set("241.1")
            untilBuild.set("253.*")
        }
    }
}
