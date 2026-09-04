import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class DateTimeValueTest {
    private static final Clock FIXED = Clock.fixed(Instant.parse("2026-09-04T10:30:00Z"), ZoneOffset.UTC);

    @Test void todayResolvesToDateOnly() {
        assertEquals("Sep 04 2026", DateTimeValue.parse("today", "deadline", FIXED).display());
    }

    @Test void nowResolvesToDateAndTime() {
        assertEquals("Sep 04 2026 10:30 AM", DateTimeValue.parse("now", "deadline", FIXED).display());
    }
}
