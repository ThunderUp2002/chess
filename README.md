# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client. The sequence diagrams for server endpoints can be found [here](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+uB5afKR3xqTsBa0ZgOnwr6zpdjACBCeKGKCcJBJEmApJvoYu40vuDJMlOylcje3l3kuwowGKEpujKcplu8SpBbyIXiVQSIbu6W4eYKw78vUh5yCgz7xOel7Xtli6VMuAZrgGJUZdZ7YWfUTnihkqgAeZjzAUmVkSWBhH6fMhmwZeVH1oZmnVBh3XwMgqYwLh+GjP1cUGah3wUaNyHjY2DGeN4fj+F4KDoDEcSJEdJ1Ob4WCiYKoH1A00gRvxEbtBG3Q9HJqgKcMG2Ieg6GWQ8sL+n9SEdSDQMefUdn2NdjlCddLlqG526eVSt45TAjJgIVxXwf9aBzsFDqhfUEVPrV8iyvKYMA2VArTdDNUvnVnYNZ1-pXaerXtRZIGTWBE29Zhs3YfNeFZnRTZ7cx-gouu-jYOKGr8WiMAAOJKhot3JZJGuvR99hKr9I2E4D2mc6WdOFPzyWpcgORazmjlos7ajIySaNZZj9LY0yeM28TiWkxVYUUyzV7aDTxpm+DDMC529SFa+9VeSHfs4+7qgYsHC6M2H9Qa0ylYINj2sJfnicpS6mtKqn7PTY1Alu9rvMIIBVvV7pYzGzmBYNOMfcoAAktIBYAIzhMEgQgps8S6igbqcqZZFjMkoBqsvkFDcsw8AHI72tFydMLibJmLYA4ZLS3D6oA9D0qY+T9Ps-LPPi-b6tMHLBvIBb0RQaa0QQHyPrRU+O1GL7QCBwAA7G4JwKAnAxAjMEOAXEABs8AJyGHdjAIol87qCweq0DoRsTbTDjugLMoC5hnympUZuNsaFKkPnQiGX4iFJxgHldE7sMRwBwe7T2qNMrp3zr5XGl58aUUJnnfc95w7ikpqzamMUbaVwUUzayycqbAG9uInyPChFKgxMPUqvsC5CmTtVd2Dca6eQ-FbeogijwoGEf+DuHCup6z6sPZ+9Qp4z3ofCC+JQr4S0Wo-OYASYBBMCNLXaTEDqWBQH2CAmxTpIASGAVJ6TMkACkIDijrnMGIf81QEPCVwmoJDmQyR6MPU2BMkJZmwAgYAqSoBwAgHZKAax-HSBCZbSG9RmFLXaZ0ygPS+mmQAOosBHm9HoAAhfiCg4AAGlviDJfsE7xDCOwOPqAAK2KWgfhRTxQeMJCjdyacMYk0zgHaRQdNEhULuFZRkdoq0yoYUBO2juEpzZg4n2TzRz+zAPwwZ8iPnWK+RKOx0cYqDPeaTe2tdkXyAMY8jOkK-BaD4aYyZXSZnQAGU-aQawApwtDgi5k2AiWGGHtqXU+o0AoEyaS2gYinGjJgFci5Sp26d0ht3IWFtpqiWvlExJUC5ZeE6VknJSr5SIGDLAYA2B2mEDyAUfBuseq1MaE9F6b0PrGClYw5xByoY6J4dwPAudcUyEsfUDgaSmRLwGDnOl5UEUZBmBAGgaV7GDjxRIowXriUu39VYyqQaQ24PrqCiNbqIUepjQVf5fr0UBsTUcZNPy03vhtQKjVeBRV2olS8YZ0rL6yqlpAoAA).

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
