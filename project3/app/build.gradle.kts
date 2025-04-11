plugins {
    application
}

repositories {
    mavenCentral()
}

val lwjglVersion = "3.3.1"

dependencies {
    // Example dependency (Guava)
    implementation("com.google.guava:guava:31.1-jre")
    
    // JOML for matrix math.
    implementation("org.joml:joml:1.10.5")

    // LWJGL BOM and core libraries.
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-stb")
    
    // Runtime native libraries for Apple Silicon on macOS.
    runtimeOnly("org.lwjgl:lwjgl::natives-macos-arm64")
    runtimeOnly("org.lwjgl:lwjgl-glfw::natives-macos-arm64")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-macos-arm64")
    runtimeOnly("org.lwjgl:lwjgl-stb::natives-macos-arm64")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("org.example.App")
    // Required on macOS.
    applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
