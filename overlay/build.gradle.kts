import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id ("com.android.application")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "com.github.kr328.mars.cutout"
        minSdk = 28
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        all {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    signingConfigs {
        all {
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
        }
    }
}

androidComponents {
    onVariants {
        afterEvaluate {
            task("packageMagisk${it.name.capitalize()}", Zip::class) {
                dependsOn(tasks["package${it.name.capitalize()}"])

                destinationDirectory.set(buildDir.resolve("outputs/magisk/${it.name}"))
                archiveBaseName.set(project.name)

                from(tasks["package${it.name.capitalize()}"]) {
                    into("system/product/overlay")
                    include("*.apk")
                    rename { "MarsCutout.apk" }
                }
                from(project.file("src/main/magisk")) {
                    expand(
                        "versionCode" to it.outputs.first().versionCode.get(),
                        "versionName" to it.outputs.first().versionName.get(),
                    )
                }

                tasks["assemble${it.name.capitalize()}"].dependsOn(this)
            }
        }
    }
}