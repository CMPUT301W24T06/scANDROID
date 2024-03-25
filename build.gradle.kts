//// Top-level build file where you can add configuration options common to all sub-projects/modules.
//plugins {
//    id("com.android.application") version "8.3.0" apply false
//
//    // Add the dependency for the Google services Gradle plugin
//    id("com.google.gms.google-services") version "4.4.1" apply false
//}

buildscript {
    repositories {
        google() // Add Google Maven repository
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.3.1")
        // Add the Google Services plugin for using Google services
        classpath("com.google.gms:google-services:4.4.1") // or the latest version available
    }
}
