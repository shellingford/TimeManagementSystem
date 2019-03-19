package tms.model.view;

import java.time.Instant;

public class TimeEntryVM {

  private String description;
  private Instant from;
  private int duration;
  private long id;
  private boolean duringPreferredTime;

  public TimeEntryVM(String description, Instant from, int duration, long id, boolean duringPreferredTime) {
    this.description = description;
    this.from = from;
    this.duration = duration;
    this.id = id;
    this.duringPreferredTime = duringPreferredTime;
  }

  public String getDescription() {
    return description;
  }

  public Instant getFrom() {
    return from;
  }

  public int getDuration() {
    return duration;
  }

  public long getId() {
    return id;
  }

  public boolean isDuringPreferredTime() {
    return duringPreferredTime;
  }

}
