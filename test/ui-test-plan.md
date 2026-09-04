# UI Test Plan

## Test settings

- Run commands from the repository root.
- Use Java 25.
- Compare stdout exactly, including whitespace and final newlines.
- Compile the Java sources into a temporary directory before running the
  program; do not add build artifacts to the repository.

## Test cases

### 1. Exit with `bye`

- **Aim:** Verify that the application displays its welcome banner, accepts the
  `bye` command, prints the farewell message, and exits.
- **Command:** `BUILD_DIR=$(mktemp -d) && javac -d "$BUILD_DIR" src/main/java/*.java && printf 'bye\n' | java -cp "$BUILD_DIR" Cheecken`
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

### 2. Exercise task commands in sequence

- **Aim:** Verify task creation, completion toggling, listing, and exit across
  the requested `todo`, `event`, `deadline`, `mark`, `unmark`, `list`, and
  `bye` commands.
- **Command:** `BUILD_DIR=$(mktemp -d) && javac -d "$BUILD_DIR" src/main/java/*.java && printf 'todo Buy milk\nevent Team meeting /from Monday 10am /to Monday 11am\ndeadline Submit report /by Friday\nmark 1\nunmark 1\nlist\nbye\n' | java -cp "$BUILD_DIR" Cheecken`
- **Input:**

  ```text
  todo Buy milk
  event Team meeting /from Monday 10am /to Monday 11am
  deadline Submit report /by Friday
  mark 1
  unmark 1
  list
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
  Got it. I've added this task:
    [T][ ] Buy milk
  Now you have 1 tasks in the list.
  ____________________________________________________________

  ____________________________________________________________
  Got it. I've added this task:
    [E][ ] Team meeting (from: Monday 10am to: Monday 11am)
  Now you have 2 tasks in the list.
  ____________________________________________________________

  ____________________________________________________________
  Got it. I've added this task:
    [D][ ] Submit report (by: Friday)
  Now you have 3 tasks in the list.
  ____________________________________________________________

  ____________________________________________________________
  Nice! I've marked this task as done:
    [T][X] Buy milk
  ____________________________________________________________
  ____________________________________________________________
  OK, I've marked this task as not done yet:
    [T][ ] Buy milk
  ____________________________________________________________
  ____________________________________________________________
  Here are the tasks in your list:
  1.[T][ ] Buy milk
  2.[E][ ] Team meeting (from: Monday 10am to: Monday 11am)
  3.[D][ ] Submit report (by: Friday)
  ____________________________________________________________
  ____________________________________________________________
  Bye. Hope to see you again soon!
  ____________________________________________________________
  ```

### 3. Reject an invalid index without changing state

- **Aim:** Confirm that an invalid `mark` is rejected and does not alter a
  previously added task.
- **Command:** `BUILD_DIR=$(mktemp -d) && javac -d "$BUILD_DIR" src/main/java/*.java && printf 'todo Read book\nmark 2\nlist\nbye\n' | java -cp "$BUILD_DIR" Cheecken`
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

### 4. Reject malformed event input without adding a task

- **Aim:** Confirm that an `event` missing `/to` is rejected and leaves the
  task list unchanged.
- **Command:** `BUILD_DIR=$(mktemp -d) && javac -d "$BUILD_DIR" src/main/java/*.java && printf 'todo Keep baseline\nevent Broken /from Monday\nlist\nbye\n' | java -cp "$BUILD_DIR" Cheecken`
- **Input:**

  ```text
  todo Keep baseline
  event Broken /from Monday
  list
  bye
  ```

- **Expected output:** After the todo acknowledgement, print the event-input
  error beginning `When your event starts la`; `list` must show exactly one
  task, `1.[T][ ] Keep baseline`; then print the normal farewell.

### 5. Reject malformed deadline input without adding a task

- **Aim:** Confirm that a `deadline` missing `/by` is rejected and does not
  mutate the task list.
- **Command:** `BUILD_DIR=$(mktemp -d) && javac -d "$BUILD_DIR" src/main/java/*.java && printf 'todo Keep baseline\ndeadline Broken\nlist\nbye\n' | java -cp "$BUILD_DIR" Cheecken`
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

### 6. Reject unknown input and preserve an existing completion state

- **Aim:** Confirm that an unknown command is rejected and does not undo a
  successful `mark` operation.
- **Command:** `BUILD_DIR=$(mktemp -d) && javac -d "$BUILD_DIR" src/main/java/*.java && printf 'todo Keep state\nmark 1\nwat\nlist\nbye\n' | java -cp "$BUILD_DIR" Cheecken`
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

### 7. Reject command-name prefixes

- **Aim:** Verify that a word beginning with a valid command name, such as
  `marker`, is not interpreted as the `mark` command.
- **Command:** `BUILD_DIR=$(mktemp -d) && javac -d "$BUILD_DIR" src/main/java/*.java && printf 'todo Keep boundary\nmarker 1\nlist\nbye\n' | java -cp "$BUILD_DIR" Cheecken`
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

### 8. Persist state-changing commands

- **Aim:** Verify that adding and toggling tasks automatically writes the
  current list to `data/cheecken.txt`.
- **Command:** Compile and run the program with `todo read book`, `mark 1`,
  `unmark 1`, then inspect `data/cheecken.txt`.
- **Input:** `todo read book`, `mark 1`, `unmark 1`, `bye`.
- **Expected output:** The file exists and contains exactly:

  ```text
  T | 0 | read book
  ```

### 9. Load persisted tasks at startup

- **Aim:** Verify that tasks already present on disk are loaded before the
  first command is processed, including their completion status.
- **Setup:** Write the following to `data/cheecken.txt` before launching:

  ```text
  T | 1 | read book
  D | 0 | return book | June 6th
  E | 0 | project meeting | Aug 6th 2-4pm
  ```

- **Command:** Compile and run `Cheecken`, send `list`, then `bye`.
- **Input:** `list`, `bye`.
- **Expected output:** The list contains three loaded tasks; `read book` is
  marked done, and the deadline and event are unmarked. No duplicate tasks are
  created before the first command.

### 10. Empty event task

- **Aim:** Reject `event /from /to` as an empty task before datetime parsing.
- **Input:** `event /from /to`.
- **Expected output:** `There's no task that is empty. LOCK INNN!` followed by
  the event example using datetimes.

### 11. Event without datetime

- **Aim:** Reject an event that has a task but no `/from` or `/to` datetime.
- **Input:** `event love me`.
- **Expected output:** `No time how I set the task...` followed by the event
  datetime example.

### 12. Event with datetime

- **Aim:** Parse and display event start/end values using 12-hour time.
- **Input:** `event love me /from 15/10/2025 0900 /to 15/10/3000 1100`.
- **Expected output:** `Got it. I've added this task:` followed by
  `[E][ ] love me (from: Oct 15 2025 9:00 AM to: Oct 15 3000 11:00 AM)`.
