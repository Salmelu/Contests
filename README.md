Contests
=======

## Introduction

Contests is a small java project created to help with organizing some contests. It is designed to ease the calculation of various competition's results and provide an easy user interface. It is also written as a client-server application, allowing multiple clients to use the same set of data.

## Purpose

This program was created as an university project. You are free to reuse or edit it any way you wish, but there is no warranty or support of any kind. For more information, see the license.

## Compatibility

This program was created under Eclipse IDE using Java 8. It also used ControlsFX library for dialogs and multicombobox, therefore you need at lease java version 8.20 to compile the project.

## Building

There is an attached buildfile which can be run using `ant`. This builds both the client and the server. Running `ant compclient`, respectively `ant compserver` builds only the respective parts.

## Running

You can run the program using `java -jar packageName.jar`. The runnables are found in the `dist/client` or `dist/server` folders after the compilation. The entry point and path to the library is set by the buildfile.  
You can also run the program directly from buildfile using `ant client` or `ant server`. Be warned that these commands run the JVM in the background.

## Documentation

You can generate javadoc documentation by running `ant doc`. If you want a detailed documentation containing all the private methods and package private classes, run `ant docall`. 

## Releases

The program is currently in development and there has been no release yet.

## Project creator

- Michal Staruch, Charles University (Czech Republic)
