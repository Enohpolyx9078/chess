# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

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
[![Sequence Diagram](Server-Logic.png)](https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVx0UBQwAA1jAAELADgwDIAR1SOUwbE43FgpXuN2eMHGc3UwHs83qYwAolBvHUYHo0bCsWowFDrtVKHd7sVzPVAk4nMNyWpVFSacsGUz6qyYOzsVyrugOLjHgS7pUbqJ6rCsjlKAAKDJGsCUTEqgCUPJEKh1Mm0ShU6nq1LAAFUBia3h8UHbZPJXZKncZagAxJCcGA+yjB4Asywwf2zMQwuGI+OwTZIMDxVMDAMwYAIWGolMoAAe2TAGkTofU-N11X1cYGiftVFELaq1FJCnLcI4lnaCPQdOrKmwBAK3b5RMqgowwqcwXFYwpUupC1ljOg9TTc1Lw8rMHk8PQ3PVmEbyjDS-7PZU9TQPgQCG7vfu97dqlqEAK0tHM-U7bQgxdB9m3uCMFA4NEcy7PVHV-KD-0A4CUAUHwCxNYBcPiRNIJDaDVHDHRangtEcILZC21Q+48SeZlzTrK01A-LBmO1J8SWZV5i3TJZaWBZYCILdoICvNAROWS4F0JAVkCFGBwlFTdj3mGBRO+CT4ikmS5P2NVOFMLxfACaB2GpGJIzgOlpDgBQYAAGQgLJChXZhiV5ZlmjaLpegMdR8jQTShM+RZvl+f5ATGbl+KdHjSUEqZhOikFYp2PZuUzBFkVRdEUA5HEUqU1sBwErdJWlPd6QPZlFWVTlEr8vtvLXMVRhqyld1pOVDxZIqWpyG8zPKp0UJQeoEA8mMTXczzrU5O1pqdP9JQ9FAwCWhatLWbKARIxQyIo+po1jJDtGTIt0pPR4YCOjV8uzAYjAgNQYDQCBmBrOs73Qx9fIdGbvo-L91r4vz6i9aZCOgJAAC8UA4KcZznQokqfTqYBFABGTdtzqgbGqPSLDEe56Pu+36YGOMAQHicaNU2mCQZfMHrvkb9UMqNmAJgIC4UtWj4nwwjiIAbkB0j-3OmBqMVyXtF5wwn3K+o2ONKAMlULjNXxRcOdSiYKeMsSxn0wzrx0+S2qqjqVNXNSNJ6rSLb0wibdku2TLMMyLL8fwvBQdAYjiRJQ-DpbfCwbypphxppDpVy6XaOluh6ELVDC4Zrek9BFOSrVTYLozoquSaOfbOb7Djk0xZ9taGPV-mgfdGBsjF7xkdRxvvcLtATqbcjYMo+B4hQEBESQAAzZXJKHmB-pyDRXo7Plslp5h9MRlHWY7sea9fGA57LAw1b7fjYfhgt99R9GUFnMLi5x52wDXQmeuJ-r93lVMd94gP12KZQ+ctgaVU5vUCwqQr5oQgZ3DgKBuCWgHkvGSI8zrj3qNIFBO1DBNyHvApipdmSx1wnrA21cTbVV-jKMYS1qRojjg7W478Sif1dt1EYYDzLeGDrCNE-hsAxkRK5OsMAADi6YNAJ1ofUBoUj05Z3sOmfOg8ZJv0qJrMkVtNHXkrobFiidQazQ8g3IhmD4Ht0QYLbuhFe4H3QQZIeWD5Y4MntPWeC8rHh1XvWTAG8cxd3Ij9XeCMoB93AadDxJ8wbnwQJfKGCi4xAJAU-F+85sbKU4V-ImtU-4NQAT4dJUSD4s1lrEyBz52ywLEFDWx1TO6ehkRSE07jHyVAjNInap4EAwDURSTQOg+wC3qBInIMAIALzLAMoZagqmj2vq3eocAICfmnpjAAPG0tQ5QSE6LIWsjZ3BwBhV2bIg5NCoGmwWaoBYDRxgLIAJLSAWCCTY8R8woBzAGXKIJkigHhH8jKCUQQLIAHLpkrp0K4OTlwf3qOpHhYx7mPOeemN5HzlhfJ+aCqK4LlhApngSmkRK0XpmhXMWFfDPACICBwAA7G4JwKAnAxDpMEOADkABs8AsLSPTDAIoH9TE1EUa0Doqj1FAJ9puKFYK2GLiOUbaq5dDEQqpUq6EFZEQojRCtMqZCVlVReL1HcDDBpNRGiVFUyqKrwCRXjN2Eo+pWrJsNNkdrWp8OrlA9sws5AoD2SaOAWE9lGrAC3UGG0j71AcQWJx-c-HDyWdg7pE84BTxnl3XxBi0Ar1rGvIJerN6wG3uE0skTonpriQG0+iTkmt1NRKtJe9ymP2nM-TGb9cmqQJgU919VrVHjKdEypAtW11KgHAxpzo7H1CDZaUNiq5idJgpmtZgqkwLKVNPHYMA8wFkGdquYdaanTXqHs-dIAdiHIeGq696ZjG8VudVV57zajwvav2l2KKFVYq-XSoOARLAoLmpsCOSAEhgHA5+CAUGABSEAYxCpPP4El8JRWcPFaSZoXogo9AWRojB6BNzYAQMAcDUB1lzSgDiylcxsXfu0Y+li5qNWyVqCsH4VGaN0egB8pjKAWO6qzIVQ1PrjVqtbabehI7PXNWk6qPtiK8kutRQp0mADlOlVU7ef1tTT4ACtUNoFDShmMkaVMxs5nGxdoSe6oecamjdY8t1eNzfPReriZJFrrOvMtITK10w7bWqd0MzFnwvg0ltUW21w3CwfTJvaEVOo04On+hSPUlPHRUvhkX4kwNnXF2NCDmmC1aemE0n73MKykX0gshg906BTG80tEmb0cE+mEumASL3swbWDd8n4H032KqoXwUAVCpdful3GIotM5cU7p21+nJ1H2nafG9sI70AhsQuyr9QzPWZq5+w6-HKBrDvfR+rnilbNaeld2AkALxT1PSePb96N6CYreRMsFZRwXgnIUIrw36hNrK-ZhLg4zyjnHDJOb2Tf1-s-iKDc2Xh06aGoDkcKZLzXkK1thLM653xdIU+9DYgbnPjuUBhYP7HYcNUgB0YIGGUhygNR6DsGvA88QHCWAwBsCUcIHkAoIr5HvsUSnNOGcs7GDY7o8Y8hVDwnqiAbgeBuRGavULbXUAFCi+QCACXaAOmHfGQboXxuxdm8xpbyLnnBd4BXjUSwBYYz2EG8fCHF5SsPt0fU19xsZd6LvTMSjBD6r1IdU7DTbPeEBw1EAA)