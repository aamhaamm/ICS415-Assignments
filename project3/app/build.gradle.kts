plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

// Define LWJGL version.
val lwjglVersion = "3.3.1"

dependencies {
    // Existing testing dependencies.
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Existing application dependency (e.g., Guava)
    implementation(libs.guava)

    // LWJGL dependencies using the BOM for version consistency.
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    // Core LWJGL libraries.
    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-opengl")
    
    // Runtime native libraries for Apple Silicon on macOS.
    runtimeOnly("org.lwjgl:lwjgl::natives-macos-arm64")
    runtimeOnly("org.lwjgl:lwjgl-glfw::natives-macos-arm64")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-macos-arm64")
}

java {
    // Define the Java toolchain version (here set to Java 21).
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    // Specify the fully qualified name of your main class.
    mainClass = "org.example.App"
    // This is the crucial change for macOS.
    applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
