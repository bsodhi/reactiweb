# README #

This project provides a starter web application that is built using
[Vertx](https://vertx.io/) at the backend. The frontend is built using
[VueJS v3](https://v3.vuejs.org/). The a starter application has the
following features:

* Gradle based build script which produces a proper output that includes
the VueJS web application complied with `npm run build` command.
* A mechanism for setting up declarative routing logic via a JSON file.
* JSON based application level settings file to allow configuring various aspects of the web application.
* A simple mechanism for role based access control. You can define the
access rules for various URIs in the routes configuration JSON file.


## Development environment setup ##

I have tested this application on Ubuntu 20.04LTS. Following are the prerequisites:
1. A git client should be available.
2. Stable version of [NodeJS](https://nodejs.dev/) should be available. Tested with version 14.17.6.
3. [Gradle](https://gradle.org/) build system.
4. An IDE to develop. I have used Visual Studio Code.

Steps to build and run:
1. Open a shell into a directory, say, `/home/auser/work`
2. Run `git clone git@github.com:bsodhi/reactiweb.git` in that shell
3. Run `cd reactiweb` to change directories into the cloned repository

Then you can execute the following commands to build, test, assemble etc.

To launch your tests:
```
./gradlew clean test
```

To package your application:
```
./gradlew clean assemble
```

To run your application:
```
./gradlew clean run
```

## Understanding the code ##

The most relevant parts of the code are shown below labeled by
a number at the right. These parts are described next in the corresponding
bullet points.

```
|-- README.md           (1)
|-- build.gradle.kts    (2)
|-- config
|   |-- routes.json     (3)
|   `-- settings.json   (4)
|-- gradle              (5)
|   `-- wrapper
|       :
|-- settings.gradle.kts
|-- src
|   |-- main/java/org
|   |               `-- gtungi
|   |                   |-- Const.java              (6)
|   |                   `-- reactiweb
|   |                       |-- App.java            (7)
|   |                       |-- WebApp.java         (8)
|   |                       |-- RbacHandler.java    (9)
|   |                       |-- ActionHandler.java  (10)
|   |                       |-- RouteConfig.java
|   |                       |-- HomeHandler.java
|   |                       `-- LoginHandler.java   
|   `-- test                                        (11)
|       `-- java
|           :
`-- web_app                                 (12)
    |-- babel.config.js
    |-- package-lock.json
    |-- package.json
    |-- public
    |   |-- favicon.ico
    |   `-- index.html
    |-- src                                 (13)
    |   |-- App.vue
    |   :
    `-- vue.config.js                       (14)
```

1. This README file.
2. The gradle build script. Please refer to the comments in this file for details about what should be changed in it when you customize the build.
3. You will define the routes in this file. The entry keys in this file are
the URI paths that the web application will handle. Each entry has the following items:
    1. `requireLogin` can be `true` or `false`.
    2. `roles` is a string with comma-separated role codes. Example: `admin,hod`
    3. `handlerClass` is the FQN of the handled class which should inherit from `ActionHandler` (see item #7 below)
    4. `isMultipart` indicates whether this request should expect a multipart HTTP request.
4. This is the application level settings file. Please see the file for details.
5. The gradle binaries and scripts go here. You do not need to edit them.
6. Define all constants that are used in the Java code in this class.
7. This class is the main entry point to the application.
8. This class contains the main logic for setting up the web application.
It includes the methods for loading the configuration files and initializing
routes, setting up the session handling and so on.
9. This class implements the RBAC logic. It is a handler which is applied to
app routes. It checks for the presence of certain keys in the session to
establish whether the currently logged in user (if any) has the necessary 
role for the requested URI path.
10. This is a convenience base class which all action handler should extend.
It provides the common functionality expected to be used by various action handlers.
11. Unit tests package.
12. The source code of the front end VueJS web application goes here.
13. The VueJS components that make the web application GUI go here.
14. This is configuration file used by VueJS build system.

## Developing frontend views and corresponding backend APIs ##
TODO:

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact