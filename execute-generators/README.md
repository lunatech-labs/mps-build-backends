# execute-generators

A command-line tool to execute MPS generator without a build script.

## Usage

The tool is JVM-based and needs MPS libraries (`${mps_home}/lib/**/*.jar`) on its classpath. The simplest way to run it
is by using Gradle's `JavaExec` task. See below for an example.

## Supported arguments

```
usage: execute-generators [-h] [--plugin PLUGIN]... [--macro MACRO]...
                          [--plugin-location PLUGIN_LOCATION] [--build-number BUILD_NUMBER]
                          --project PROJECT [--test-mode] [--environment ENVIRONMENT]
                          [--log-level LOG_LEVEL] [--model MODEL]... [--module MODULE]...
                          [--exclude-model EXCLUDE_MODEL]... [--exclude-module EXCLUDE_MODULE]...
                          [--parallel-generation-threads THREADS] [--no-strict-mode]

required arguments:
  --project PROJECT                       project to generate from


optional arguments:
  -h, --help                              show this help message and exit
        
  --plugin PLUGIN                         plugin to to load. The format is --plugin=<id>::<path>
        
  --macro MACRO                           macro to define. The format is --macro=<name>::<value>
        
  --plugin-location PLUGIN_LOCATION       location to load additional plugins from
        
  --build-number BUILD_NUMBER             build number used to determine if the plugins are
                                          compatible
        
  --test-mode                             run in test mode
        
  --environment ENVIRONMENT               kind of environment to initialize, supported values are
                                          'idea' (default), 'mps'

  --log-level LOG_LEVEL                   console log level. Supported values: info, warn, error,
                                          off. Default: warn.
        
  --model MODEL                           list of models to generate
        
  --module MODULE                         list of modules to generate
        
  --exclude-model EXCLUDE_MODEL           list of models to exclude from generation
        
  --exclude-module EXCLUDE_MODULE         list of modules to exclude from generation

  --no-strict-mode                        Disable strict generation mode. Strict mode places
                                          additional limitations on generators, but is required
                                          for parallel generation

  --parallel-generation-threads THREADS   Number of threads to use for parallel generation. Value
                                          of 0 means that parallel generation is disabled.
                                          Default: 0
```

## Error exit codes

* `254`: nothing to generate
* `255`: general MPS error

## Gradle example (Kotlin syntax)

```kotlin
val mps by configurations.creating
val executeGenerators by configurations.creating

dependencies {
    mps("com.jetbrains:mps:$mpsVersion@zip")
    executeGenerators("de.itemis.mps.build-backends:execute-generators:$buildBackendsVersion")
}

val mpsHome = File(buildDir, "mps")

val unpackMps by tasks.registering(Sync::class) {
    dependsOn(configuration)
    from({ configuration.resolve().map(project::zipTree) })
    into(mpsHome)
}

val generate by tasks.registering(JavaExec::class) {
    dependsOn(unpackMps)
    classpath(executeGenerators)
    classpath(fileTree(mpsHome) {
        include("lib/**/*.jar")
    })

    mainClass.set("de.itemis.mps.gradle.generate.MainKt")

    args("--project", it.projectDir)
    args("--model", "my.model.to.generate")
}
```
