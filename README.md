# README - SOPRA FS20 Group 17

## Introduction

The Project aim is to provide the server side infrastrucute for our Just One Web Application. It mainly provides an API interface
and includes the business logic of our game implementation.

## Launch & Deployment

### Setup this Template with your IDE of choice

Download your IDE of choice: (e.g., [Eclipse](http://www.eclipse.org/downloads/), [IntelliJ](https://www.jetbrains.com/idea/download/)) and make sure Java 13 is installed on your system.

1. File -> Open... -> SoPra Server Template
2. Accept to import the project as a `gradle project`

To build right click the `build.gradle` file and choose `Run Build`

### Building with Gradle

You can use the local Gradle Wrapper to build the application.

Plattform-Prefix:

-   MAC OS X: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

#### Build

```bash
./gradlew build
```

#### Run

```bash
./gradlew bootRun
```

#### Test

```bash
./gradlew test
```

#### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

### Debugging

If something is not working and/or you don't know what is going on. We highly recommend that you use a debugger and step
through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command),
do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug"Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

### Testing
Have a look here: https://www.baeldung.com/spring-boot-testing

## Technologies

For our backend we mainly used [Java Springboot](https://spring.io/projects/spring-boot), JPA (i.e. [Hibernate](https://hibernate.org/)) and a [H2 temporary database](https://www.h2database.com/html/main.html).
As our IDE choice we use [Jetbrains Intellij](https://www.jetbrains.com/de-de/idea/).

## High Level Components

User/Player-Component
> Such that a registered individual can be identified, a user is created with a corresponding profile. A user profile can be modified. 
> As soon as a user joins
> a game, a player is automatically created. This seperation to enable the later extension of a user playing multiple games at once.
> Remind that this is currently not implemented.
>
> - [User entity](https://github.com/SOPRA-Group-17/sopra-fs-20-group17-server/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/entity/User.java)
> - [Player entity](https://github.com/SOPRA-Group-17/sopra-fs-20-group17-server/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/entity/Player.java)

Gameservice
> The game service provides all functionalities to create, start, alter and delete a game.
> Additionally, it handles the the cards of any game, such as draw a new one. A game is composed
of several rounds.
> - [GameService](https://github.com/SOPRA-Group-17/sopra-fs-20-group17-server/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/service/GameService.java)

Roundservice
> The roundservice includes the major business logic. It handles hints, terms and guesses of players and coordinates
their validation using the validator classes. Moreover, the roundservice manages the states of specific game.
> - [RoundService](https://github.com/SOPRA-Group-17/sopra-fs-20-group17-server/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/service/RoundService.java)

Validators
> Two main validator class are responsible for validating hints and guesses. The hint validation makes use of an
>external API for NLP processing.
> - [HintValidator](https://github.com/SOPRA-Group-17/sopra-fs-20-group17-server/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/helper/HintValidator.java)
> - [GuessValidator](https://github.com/SOPRA-Group-17/sopra-fs-20-group17-server/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/helper/GuessValidator.java)

### External Dependencies

#### Accessing H2 Database

In development enviroment, the H2 database can be accessed locally through the browser using this link:
`http://localhost:8080/h2-console/

The database is initially loaded with Card entities using our 
[Dataloader Class](https://github.com/SOPRA-Group-17/sopra-fs-20-group17-server/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/DataLoader.java).

### Releases

A new release of the application can be published using Github [here](https://github.com/SOPRA-Group-17/sopra-fs-20-group17-server/releases)

## Roadmap

As new developer joining this project, there a few features on the list:

- Chat functionality, to improve interaction opportunities of players
- Bots, for lonely players
- Extended hint validaiton for foreign languages
- Exchange in-memory DB with persistent DB

## Authors and Acknowledgement

* Backend Developers:
    - [Janosch Baltensperger](https://github.com/janousy), BSc Student University of Zurich
    - [Domenic Luca Fuerer](https://github.com/dofuerer), BSc Student University of Zurich
    
* Supervisor and Tutor:
    - [Nik Zaugg](https://github.com/nikzaugg), MSc Student University of Zurich
    
## Licence

### Apache

[Apache Licence 2.0](https://github.com/SOPRA-Group-17/sopra-fs-20-group17-server/blob/master/LICENSE)

### MIT License

Copyright (c) [2020] [Janosch Baltensperger, Domenic Luca Fuerer]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
