package tms.model.entity;

import static org.junit.Assert.assertEquals;

import java.time.Instant;

import org.junit.Test;

import tms.model.entity.TimeEntry;

public class TimeEntryTest {

  @Test
  public void testConstructor() {
    Long id = 1L;
    String description = "testDescr";
    Instant startTime = Instant.now();
    Integer duration = 55;
    Long userId = 3L;

    TimeEntry timeEntry = new TimeEntry(id, description, startTime, duration, userId);

    assertEquals(id, timeEntry.getId());
    assertEquals(description, timeEntry.getDescription());
    assertEquals(startTime, timeEntry.getStartTime());
    assertEquals(duration, timeEntry.getDuration());
    assertEquals(userId, timeEntry.getUserId());
  }

  @Test
  public void testUpdate() {
    String description = "testDescr";
    Instant startTime = Instant.now();
    Integer duration = 55;
    Long userId = 3L;

    TimeEntry timeEntry = new TimeEntry(description, startTime, duration, userId);

    duration = 45;
    description = "testDescr_updated";
    timeEntry.update(description, startTime, duration);

    assertEquals(description, timeEntry.getDescription());
    assertEquals(startTime, timeEntry.getStartTime());
    assertEquals(duration, timeEntry.getDuration());
  }
}
