import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "org.gtungi"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.1.4"
val junitJupiterVersion = "5.7.0"

val mainClassName = "org.gtungi.reactiweb.App"
/**
Name of static web contents folder. The value of this setting and that of
the "staticWebDir" key in config/settings.json must match.
*/
val staticWebDir = "static_content"

application {
  mainClass.set("$mainClassName")
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-sql-client-templates")
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-mysql-client")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
  implementation("com.google.code.gson:gson:2.8.8")
}

distributions {
    main {
        contents {
            from("$projectDir/config")
        }
    }
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

sourceSets {
  main {
    resources {
      /**
      Include the output of VueJS production build produced
      by the compileWebApp task.
      */
      srcDir("$projectDir/web_app/out")
    }
  }
}

tasks.named("processResources") {
  /* Build the VueJS web app. */
  dependsOn("compileWebApp")
}

/**
This task creates a production build for the VueJS web application.
The output will be included in Java resources.
*/
tasks.register("compileWebApp") {
  doFirst {
    println(">>> Generating web app")
    exec {
      workingDir(project.file("web_app"))
      commandLine("npm", "install")
    }
    // exec {
    //   workingDir(project.file("web_app"))
    //   commandLine("npm", "audit", "fix")
    // }
    exec {
      workingDir(project.file("web_app"))
      commandLine("npm", "run", "build")
    }

    copy {
      from("$projectDir/web_app/dist")
      into("$projectDir/web_app/out/$staticWebDir")
    }
  }
}

tasks.named("clean") {
  doLast {
    delete("$projectDir/web_app/dist")
    delete("$projectDir/web_app/out")
    println("Deleted the web app build.")
  }
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Class" to mainClassName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  // args = listOf("run", mainClassName, "--redeploy=$watchForChange", "--on-redeploy=$doOnChange")
  main = mainClassName
  classpath = sourceSets["main"].runtimeClasspath
  args = listOf("$projectDir/config/settings.json")
}
