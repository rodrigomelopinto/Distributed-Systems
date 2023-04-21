package pt.ulisboa.tecnico.classes;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

/**
 * It converts a LocalDateTime to a String in ISO format, and it converts a String in ISO format to a
 * LocalDateTime
 */
public class DateHandler {

  private DateHandler() {}

  public static String toISOString(LocalDateTime time) {
    if (time == null) {
        return null;
    }
    return ZonedDateTime.of(time, ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT);
  }

  public static LocalDateTime toLocalDateTime(String date) {
    try {
        return ZonedDateTime.parse(date).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    } catch (Exception e) {
        return null;
    }
  }
}