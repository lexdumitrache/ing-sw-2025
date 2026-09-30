# Compile
`mvn clean package`
# Run
Make sure you're in the main directory
## Server
`java -jar target/GC06-1.0-Server.jar` will start the server making you choose the network interface and on default TCP and RMI ports
### Options
`--hostname name` will use `name` as hostname\

`--tcp-port port` will use `port` as the port for TCP connections (1234 if not provided)\
`--rmi-port port` will use `port` as the port for RMI connections (1099 if not provided)
## Client
`java -jar target/GC06-1.0-Client.jar` will open the TUI client
### Options
`--useRMI` will skip protocol selection and use RMI\
`--useTCP` will skip protocol selection and use TCP
#### Modifier
`--localhost` is a modifier of the previous two, and it will try to connect to localhost:defaultport automatically

# Code coverage
`mvn clean verify` and then open `target/site/jacoco/index.html`

# What we implemented
- Volo di Prova
- Partite Multiple
- Regole complete + TUI + RMI + Socket + 2FA

## How to Play – TUI Client Guide

This guide will help you get the most out of the TUI (Text User Interface) client.

### Basic Commands

In general, commands are structured with an **arguments list** that you can fill in one at a time:

![Commands Example](https://github.com/user-attachments/assets/2f180eea-d2ec-4cc2-86c5-072490cdd7d7)

Alternatively, you can input the full command with all necessary arguments in one line, for example:

```
declarefirepower 7
```

---

### Building Phase

During the **building phase**, all standard commands are available, along with visual aids to help with placement and planning:

![Building Phase](https://github.com/user-attachments/assets/51953cf6-ce4c-4666-b484-a56b7d76f1cc)

Below the ship layout, you’ll also find a small **legend** that explains the symbols used.

**Important Enums:**

**Origin** : Hand, First_Reserved, Second_Reserved

**Orientation**: Up, Down, Left, Right

### Flight Phase

In the **flight phase**, additional visualizers become available to assist during gameplay. For example:

![Flight Phase](https://github.com/user-attachments/assets/3d9e9ed7-9520-4360-919e-1647fea6e36b)

The current turn is displayed under the flight board.
When resolving a card, an arrow highlights the active player for that turn.
(Indexes are from 0)

## UML
For better clarity, the low level UML is divided into Model, View, Controller, Networking


