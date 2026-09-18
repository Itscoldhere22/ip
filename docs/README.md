# Cheecken User Guide

Cheecken remembers your to-dos, deadlines, and events. Type a command and press
Enter or click Send. Use `list` to see your tasks and their numbers.

## Adding deadlines

```text
deadline submit report /by tomorrow
deadline send email /by today 1800
deadline start work /by now
```

On Monday, 7 September 2026, the first command adds a deadline displayed as
`[D][ ] submit report (by: Sep 08 2026)`. The second sets 6:00 PM that Monday,
even if you enter it after 6:00 PM. `now` records the current local date and time.

## Adding events

```text
event study group /from Mon 0900 /to Monday 1000
event trip /from tomorrow /to Sunday
```

If you enter the first command on Monday, 7 September 2026, it adds an event
for Monday, 14 September, from 9:00 AM to 10:00 AM. Each endpoint resolves
independently from the submission date. Cheecken does not reject an end before
the start or move the end automatically; check your entered dates.

## Accepted dates and times

| Input | Meaning |
|---|---|
| `today` | The local submission date, with no time |
| `tomorrow` | The following local calendar date |
| `Mon`, `Monday` | The strictly next Monday, never today |
| `Tue`, `Wed`, `Thu`, `Fri`, `Sat`, `Sun` | The strictly next matching weekday |
| `Tuesday` through `Sunday` | Full names are also accepted |
| `today 0900`, `tomorrow 1800`, `Mon 0000` | The resolved date at the supplied 24-hour time |
| `now` | Current local date and time; do not append another time |
| `15/10/2026`, `15/10/2026 1800` | Existing numeric date and optional time |
| `2026-10-15`, `2026-10-15T18:00` | ISO date or date-time |

Date words are case-insensitive: `MONDAY` and `monday` mean the same thing.
Keep `/by`, `/from`, and `/to` lowercase, separated from their surrounding text.
Extra whitespace around date words or before their time is accepted.
Natural-date time suffixes must have four digits from `0000` through `2359`;
the minutes must be between `00` and `59`.

`Tues`, `Thurs`, `Mon.`, `yesterday`, `next Monday`, `next week`, `in 3 days`,
`today 09:00`, and `tomorrow 6pm` are not supported. An invalid date produces
a short example and leaves the task list unchanged.

Dates use your computer's local timezone when you submit the command. Both
event dates use one clock reading, even if the command crosses midnight.
Past dates and times are allowed. No time is implied for a date-only input.
Dates display as `Sep 08 2026`; explicit times display as `Sep 08 2026 6:00 PM`.

## Saving and reopening

Tasks save automatically to `data/cheecken.txt` in the launch directory.
Natural dates become absolute dates when entered: a saved `tomorrow` does not
move each day. Date-only values stay date-only after reopening, while explicit
midnight remains `12:00 AM`. Older saved deadlines with midnight timestamps
keep that time because Cheecken cannot tell whether midnight was intentional.

Existing absolute records remain readable. Natural expressions manually placed
in the storage file are skipped as malformed records. Use commands to create
relative dates instead of editing the file.

## Other commands

Use `todo read a book`, `mark 1`, `unmark 1`, `delete 1`, or `find book` to manage
tasks. Task numbers come from `list`. `bye` closes the application. The command
guide in the window provides examples, and the [README](../README.md) explains
how to run the GUI or console interface.
