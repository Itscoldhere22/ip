# Cheecken project template

This is a project template for a greenfield Java project. It's named after the Java mascot _Cheecken_. Given below are instructions on how to use it.

## Checking Java coding style

Checkstyle uses the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
configuration from [AddressBook Level 3](https://github.com/se-edu/addressbook-level3/tree/master/config/checkstyle).
The rules are in `config/checkstyle/checkstyle.xml`; `suppressions.xml` contains the upstream exceptions for test Javadocs.

With JDK 25 configured, check application and test sources using:

```bash
./gradlew checkstyleMain checkstyleTest --continue
```

On Windows, use `gradlew.bat` instead of `./gradlew`. The `--continue` option lets both checks run even if one fails.
Reports are generated in `build/reports/checkstyle/main.html` and `build/reports/checkstyle/test.html`.
Checkstyle also runs as part of `./gradlew check` and `./gradlew build`. Existing style violations must be fixed
for these checks to pass.

For editor feedback, install the Checkstyle-IDEA plugin in IntelliJ, select Checkstyle **14.1.0** to match
`build.gradle`, add `config/checkstyle/checkstyle.xml` as an active local configuration, and set the scan scope
to **Only Java sources (including tests)**. See the [SE-EDU tutorial](https://se-education.org/guides/tutorials/checkstyle.html)
for the full setup instructions.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/cheecken/Cheecken.java` file, right-click it, and choose `Run Cheecken.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   ___ _                    _              
  / __\ |__   ___  ___  ___| | _____ _ __  
 / /  | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
/ /___| | | |  __/  __/ (__|   <  __/ | | |
\____/|_| |_|\___|\___|\___|_|\_\___|_| |_|
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
