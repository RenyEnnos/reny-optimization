import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.api.tasks.WriteProperties
import org.gradle.jvm.tasks.Jar

plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

// The bridge is compiled from the generic toolkit only when requested. It is
// deliberately excluded from the mod jar: the running toolkit owns these
// classes, while Reny only owns its provider/extension.
val bridgeRequested = providers.gradleProperty("minecraft.dev.bridge")
    .map(String::toBoolean)
    .orElse(false)
    .get() || System.getProperty("minecraft.dev.bridge").toBoolean()
val bridgeHome = providers.environmentVariable("MINECRAFT_DEV_TOOLKIT_HOME")
    .map { file(it) }
    .orElse(file("../minecraft-dev-toolkit"))
val bridgeSources = bridgeHome.map { it.resolve("bridge/forge-1.7.10/src/main/java") }
if (bridgeRequested && !bridgeSources.get().isDirectory) {
    throw GradleException("minecraft.dev.bridge requested, but bridge sources were not found. Set MINECRAFT_DEV_TOOLKIT_HOME or provide sibling minecraft-dev-toolkit.")
}
if (bridgeSources.get().isDirectory) {
    sourceSets["main"].java.srcDir(bridgeSources)
} else {
    // The normal mod remains buildable without the optional toolkit checkout.
    sourceSets["main"].java.exclude("dev/reny/optimization/devbridge/**")
}
tasks.withType<Jar>().configureEach {
    exclude("dev/reny/minecraftdev/bridge/**")
    exclude("dev/reny/optimization/devbridge/**")
}

val generatedRenyBuildInfo = layout.buildDirectory.file("generated-resources/reny/reny-build.properties")
val explicitRenyCommitSha = providers.environmentVariable("RENY_COMMIT_SHA")
    .orElse(providers.environmentVariable("GITHUB_SHA"))
    .map(String::trim)
    .filter { it.isNotEmpty() }
val discoveredRenyCommitSha = providers.exec {
    commandLine("git", "rev-parse", "HEAD")
}.standardOutput.asText
    .map(String::trim)
    .filter { it.isNotEmpty() }
val renyCommitSha = explicitRenyCommitSha
    .orElse(discoveredRenyCommitSha)
    .orElse("unknown")

val writeRenyBuildInfo = tasks.register<WriteProperties>("writeRenyBuildInfo") {
    destinationFile.set(generatedRenyBuildInfo)
    property("commit_sha", renyCommitSha)
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(writeRenyBuildInfo)
    from(generatedRenyBuildInfo)
}

// The project deliberately uses dependency-free executable self-tests instead of a JUnit engine.
// Gradle 9 otherwise fails when it sees test sources but discovers no framework-managed tests.
tasks.withType<org.gradle.api.tasks.testing.AbstractTestTask>().configureEach {
    failOnNoDiscoveredTests = false
}

tasks.register<JavaExec>("patchRegistrySelfTest") {
    group = "verification"
    description = "Runs the dependency-free Patch Registry self-test suite."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("dev.reny.optimization.patch.PatchRegistrySelfTest")
}

tasks.register<JavaExec>("compatibilitySelfTest") {
    group = "verification"
    description = "Runs the dependency-free environment/compatibility self-test suite."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("dev.reny.optimization.compat.CompatibilityManagerSelfTest")
}

tasks.register<JavaExec>("profilerSelfTest") {
    group = "verification"
    description = "Runs the dependency-free internal profiler self-test suite."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("dev.reny.optimization.profiler.InternalProfilerSelfTest")
}

tasks.register<JavaExec>("renyProfilerJsonSelfTest") {
    group = "verification"
    description = "Runs the dependency-free Reny bridge profiler JSON self-test."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("dev.reny.optimization.devbridge.RenyProfilerJsonSelfTest")
}

tasks.register<JavaExec>("benchmarkHarnessSelfTest") {
    group = "verification"
    description = "Runs the dependency-free benchmark harness self-test suite."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("dev.reny.optimization.profiler.BenchmarkHarnessSelfTest")
}

tasks.register<JavaExec>("benchmarkControllerSelfTest") {
    group = "verification"
    description = "Runs the dependency-free benchmark controller/state integration self-test suite."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("dev.reny.optimization.benchmark.BenchmarkControllerSelfTest")
}

tasks.register<JavaExec>("profilerOverheadBenchmark") {
    group = "verification"
    description = "Runs the informational internal-profiler overhead microbenchmark."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("dev.reny.optimization.profiler.ProfilerOverheadBenchmark")
}

tasks.named("check") {
    dependsOn(
        "patchRegistrySelfTest",
        "compatibilitySelfTest",
        "profilerSelfTest",
        "renyProfilerJsonSelfTest",
        "benchmarkHarnessSelfTest",
        "benchmarkControllerSelfTest")
}
