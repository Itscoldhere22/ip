# UI Test Plan

## Test settings

- Run commands from the repository root.
- Use Java 25.
- In expected-output blocks, `\033` represents the actual ANSI escape
  character (U+001B), not four literal characters. Decode only this notation
  before comparing; preserve the ASCII-art backslashes literally.
- Ignore trailing ASCII spaces on the first six stdout lines (the decorative
  welcome banner), on both actual and expected output. Leading spaces and
  spaces within those lines remain significant.
- Compare all remaining stdout exactly, including ANSI formatting sequences,
  whitespace, blank lines, and the final newline. Require empty stderr and
  exit code 0 unless a case explicitly specifies otherwise.
- Compile with `./gradlew classes` so JavaFX dependencies are available.
- Run each console case in a fresh temporary working directory, using an
  absolute classpath to `build/classes/java/main`. Never use real task data.
- The console entry point remains `cheecken.Cheecken`; the GUI uses
  `cheecken.Launcher`.

## JavaFX acceptance test

- **Aim:** Exercise the real chat window and preserve the existing task commands.
- **Command:** `./gradlew guiTest` (Java 25 and a graphical desktop required).
- **Setup:** The test supplies its own temporary persistence file. It does not
  read or modify `data/cheecken.txt` in the project.
- **Input/actions, in order:**
  1. Open the window: greeting visible; empty input disables Send.
  2. Submit spaces: no message is added.
  3. Enter `todo GUI test task`: one user message and an added-task reply;
     input clears.
  4. Click Send for `mark 1`: reply includes `[T][X] GUI test task`.
  5. Add a deadline and event using `15/10/2026 1800` and
     `/from 15/10/2026 0900 /to 15/10/2026 1000`: dates display in 12-hour format.
  6. Enter `wat`, then `find GUI`: an error appears and the next command works.
  7. Expand Command guide: command examples and date format are visible.
  8. Submit a long task and repeated `list` commands; resize to 440 × 560:
     messages wrap, the composer remains usable, and the conversation scrolls
     to the latest response.
  9. Enter `bye`: the window closes immediately.
  10. Open a new window using the same temporary file and enter `list`:
      the first task is still marked complete. Submit `bye` using Send and
      verify that this also closes the window.
- **Expected result:** All assertions pass. A rendered scene snapshot is saved
  to the Java temporary directory as `cheecken-gui.png` for visual review.
  JavaFX may issue a Java 25 native-access warning on stderr; this is not a
  command response or a failed assertion.

## Legacy console test cases

These cases predate the GUI. Some cases still describe older error messages;
do not silently adjust their expected results when running regression checks.
Stop and report the first mismatch.

Unsupported natural-language date scenarios have been removed. The separate
`DateTimeValueTest` unit tests retain coverage for the date helper’s supported
`today` and `now` keywords; these are not supported deadline commands.

### 1. Exit with `bye`

- **Aim:** Verify that the application displays its welcome banner, accepts the
  `bye` command, prints the farewell message, and exits.
- **Command:** `./gradlew classes && CLASS_DIR="$PWD/build/classes/java/main" && TEST_DIR=$(mktemp -d) && cd "$TEST_DIR" && printf 'bye\n' | java -cp "$CLASS_DIR" cheecken.Cheecken`
- **Input:**

  ```text
  bye
  ```

- **Expected output:**

  ```text
   _____ _                    _
  /  __ \ |                  | |
  | /  \/ |__   ___  ___  ___| | _____ _ __
  | |   | '_ \ / _ \/ _ \/ __| |/ / _ \ '_ \
  | \__/\ | | |  __/  __/ (__|   <  __/ | | |
   \____/_| |_|\___|\___|\___|_|\_\___|_| |_|
  ____________________________________________________________
  Hello! I'm \033[3mCHEECKEN\033[0m.
  What can I do for you?
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

### 2. Reject an invalid index without changing state

- **Aim:** Confirm that an invalid `mark` is rejected and does not alter a
  previously added task.
- **Command:** `./gradlew classes && CLASS_DIR="$PWD/build/classes/java/main" && TEST_DIR=$(mktemp -d) && cd "$TEST_DIR" && printf 'todo Read book\nmark 2\nlist\nbye\n' | java -cp "$CLASS_DIR" cheecken.Cheecken`
- **Input:**

  ```text
  todo Read book
  mark 2
  list
  bye
  ```

- **Expected output:** The normal welcome banner and todo acknowledgement;
  then the Java index error message; then `list` must show exactly one task,
  `1.[T][ ] Read book`, still unmarked; finally the normal farewell.

### 3. Reject malformed event input without adding a task

- **Aim:** Confirm that an `event` missing `/to` is rejected and leaves the
  task list unchanged.
- **Command:** `./gradlew classes && CLASS_DIR="$PWD/build/classes/java/main" && TEST_DIR=$(mktemp -d) && cd "$TEST_DIR" && printf 'todo Keep baseline\nevent Broken /from 15/10/2026 0900\nlist\nbye\n' | java -cp "$CLASS_DIR" cheecken.Cheecken`
- **Input:**

  ```text
  todo Keep baseline
  event Broken /from 15/10/2026 0900
  list
  bye
  ```

- **Expected output:** After the todo acknowledgement, print the event-input
  error beginning `When your event starts la`; `list` must show exactly one
  task, `1.[T][ ] Keep baseline`; then print the normal farewell.

### 4. Reject malformed deadline input without adding a task

- **Aim:** Confirm that a `deadline` missing `/by` is rejected and does not
  mutate the task list.
- **Command:** `./gradlew classes && CLASS_DIR="$PWD/build/classes/java/main" && TEST_DIR=$(mktemp -d) && cd "$TEST_DIR" && printf 'todo Keep baseline\ndeadline Broken\nlist\nbye\n' | java -cp "$CLASS_DIR" cheecken.Cheecken`
- **Input:**

  ```text
  todo Keep baseline
  deadline Broken
  list
  bye
  ```

- **Expected output:** After the todo acknowledgement, print the deadline
  error beginning `What's a deadlined task without the deadline??`; `list`
  must show exactly one task, `1.[T][ ] Keep baseline`; then print the normal
  farewell.

### 5. Reject unknown input and preserve an existing completion state

- **Aim:** Confirm that an unknown command is rejected and does not undo a
  successful `mark` operation.
- **Command:** `./gradlew classes && CLASS_DIR="$PWD/build/classes/java/main" && TEST_DIR=$(mktemp -d) && cd "$TEST_DIR" && printf 'todo Keep state\nmark 1\nwat\nlist\nbye\n' | java -cp "$CLASS_DIR" cheecken.Cheecken`
- **Input:**

  ```text
  todo Keep state
  mark 1
  wat
  list
  bye
  ```

- **Expected output:** The todo acknowledgement and successful mark message;
  then the unknown-command error beginning `What do you want?`; `list` must
  show exactly one completed task, `1.[T][X] Keep state`; then print the normal
  farewell.

### 6. Reject command-name prefixes

- **Aim:** Verify that a word beginning with a valid command name, such as
  `marker`, is not interpreted as the `mark` command.
- **Command:** `./gradlew classes && CLASS_DIR="$PWD/build/classes/java/main" && TEST_DIR=$(mktemp -d) && cd "$TEST_DIR" && printf 'todo Keep boundary\nmarker 1\nlist\nbye\n' | java -cp "$CLASS_DIR" cheecken.Cheecken`
- **Input:**

  ```text
  todo Keep boundary
  marker 1
  list
  bye
  ```

- **Expected output:** The todo acknowledgement; then the unknown-command
  error beginning `What do you want?`; `list` must show exactly one unmarked
  task, `1.[T][ ] Keep boundary`; then the normal farewell.

### 7. Persist state-changing commands

- **Aim:** Verify that adding and toggling tasks automatically writes the
  current list to `data/cheecken.txt`.
- **Command:** Compile and run the program with `todo read book`, `mark 1`,
  `unmark 1`, then inspect `data/cheecken.txt`.
- **Input:** `todo read book`, `mark 1`, `unmark 1`, `bye`.
- **Expected output:** The file exists and contains exactly:

  ```text
  T | 0 | read book
  ```

### 8. Empty event task

- **Aim:** Reject `event /from /to` as an empty task before datetime parsing.
- **Input:** `event /from /to`.
- **Expected output:** `There's no task that is empty. LOCK INNN!` followed by
  the event example using datetimes.

### 9. Find tasks by keyword

- **Aim:** Verify that `find <keyword>` displays every task whose description
  contains the keyword, while preserving the task-list order and formatting.
- **Input:**

  ```text
  todo read book
  deadline return book /by 15/10/2025
  find book
  bye
  ```

- **Expected output:** The response begins `Here are the matching tasks in
  your list:` and includes `1.[T][ ] read book` and
  `2.[D][ ] return book`.

### 10. Case-insensitive keyword search

- **Aim:** Verify that searching is case-insensitive.
- **Input:** `todo Read Book`, then `find book`, then `bye`.
- **Expected output:** The result includes `1.[T][ ] Read Book`.

### 11. Find with no matching tasks

- **Aim:** Verify that a search with no matches does not mutate the task list
  and reports that no tasks match.
- **Input:** `todo buy flowers`, then `find book`, then `bye`.
- **Expected output:** `There are no matching tasks in your list.`

### 12. Find without a keyword

- **Aim:** Verify that the incomplete `find` command is rejected clearly.
- **Input:** `todo read book`, then `find`, then `bye`.
- **Expected output:** `Please provide a keyword to search for.`

### 13. Event without datetime

- **Aim:** Reject an event that has a task but no `/from` or `/to` datetime.
- **Input:** `event love me`.
- **Expected output:** `No time how I set the task...` followed by the event
  datetime example.

### 14. Event with datetime

- **Aim:** Parse and display event start/end values using 12-hour time.
- **Input:** `event love me /from 15/10/2025 0900 /to 15/10/3000 1100`.
- **Expected output:** `Got it. I've added this task:` followed by
  `[E][ ] love me (from: Oct 15 2025 9:00 AM to: Oct 15 3000 11:00 AM)`.
