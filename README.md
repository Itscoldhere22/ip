# Cheecken

Cheecken is a JavaFX chatbot for managing to-dos, deadlines, and events. Type a command,
press **Enter** or click **Send**, and read the reply in the conversation.

## Run the app

Use **JDK 25**. On macOS, use the JavaFX-bundled distribution recommended by the
[SE-EDU tutorial](https://se-education.org/guides/tutorials/javaFxPart1.html#setting-up-java-fx).
For this project, the SDKMAN installation is `25.0.3.fx-zulu`:

```bash
sdk use java 25.0.3.fx-zulu
./gradlew run
```

In IntelliJ IDEA, open the project as a Gradle project, select JDK 25 for both the project
and Gradle JVM, reload Gradle, and run `cheecken.Launcher`.
On Windows, replace `./gradlew` with `gradlew.bat`.

To build and launch a standalone JAR:

```bash
./gradlew shadowJar
java -jar build/libs/cheecken-1.0-SNAPSHOT-all.jar
```

The Gradle configuration follows the tutorial's JavaFX **17.0.7** dependencies for
Windows, macOS, and Linux, including base, controls, FXML, and graphics. The GUI uses
Java layout code rather than FXML so event handling and layout are easy to trace.
The separate `Launcher` is the entry point recommended by the tutorial.
Desktop verification was performed on macOS with the specified Java 25 distribution.

## Using Cheecken

Open **Command guide** in the window for examples. Commands include:

```text
todo read a book
deadline submit report /by 15/10/2026 1800
event meeting /from 15/10/2026 0900 /to 15/10/2026 1000
list
find book
mark 1
unmark 1
delete 1
bye
```

Task numbers start at 1; use `list` to see them. Dates accept `d/M/yyyy` and optional
24-hour `HHmm` time. Blank messages are ignored. Replies wrap to fit the window and
scroll into view. `bye` closes the window and exits the app. Closing the window directly is also safe: task changes are saved after each command.

Tasks are stored in `data/cheecken.txt`, relative to the directory from which you launch
the application. Reopen from the same directory to load the same tasks. Storage errors
appear in the conversation. The conversation history itself is not persisted.

## Understanding the code

- `Launcher`: starts JavaFX.
- `Main`: builds the window and forwards input to `Cheecken.getResponse`.
- `Cheecken`: handles commands and maintains one chatbot session's state.
- `Ui`: formats responses and sends them to the supplied output destination.
- `Storage`: loads and saves tasks and reports file errors to the same destination.
- `src/main/resources/styles/chat.css`: controls colours, spacing, and fonts.

The console interface remains available for debugging:

```bash
./gradlew runCli
```

## Tests and coding style

```bash
./gradlew check
./gradlew guiTest
```

`check` runs the normal automated tests and Checkstyle for application and test code.
`guiTest` is an additional opt-in JavaFX acceptance test requiring a graphical desktop.
It uses temporary task data to exercise Enter, Send, blank input, error recovery, command
help, scrolling, resizing, exit behaviour, and persistence after reopening.
See `test/ui-test-plan.md` for the acceptance plan and the legacy console cases.

Checkstyle **14.1.0** uses the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
configuration from [AddressBook Level 3](https://github.com/se-edu/addressbook-level3/tree/master/config/checkstyle).
Run only the style checks with:

```bash
./gradlew checkstyleMain checkstyleTest --continue
```

Reports are in `build/reports/checkstyle/` and `build/reports/tests/`.
For editor feedback, install Checkstyle-IDEA, select version **14.1.0**, activate
`config/checkstyle/checkstyle.xml`, and include test sources in the scan scope.
