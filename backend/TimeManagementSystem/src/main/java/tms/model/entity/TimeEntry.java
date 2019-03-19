package tms.model.entity;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class TimeEntry extends AbstractPersistable<Long> {

  private String description;
  private Instant startTime;
  private Integer duration;
  private Long userId;

  @Transient
  private boolean duringPreferredTime = false;

  protected TimeEntry() {
  }

  public TimeEntry(Long id, String description, Instant startTime, Integer duration, Long userId) {
    if (description != null)
      this.description = description;
    if (startTime != null)
      this.startTime = startTime;
    if (duration != null)
      this.duration = duration;
    if (userId != null)
      this.userId = userId;
    if (id != null)
      this.setId(id);
  }

  public TimeEntry(String description, Instant startTime, int duration, long userId) {
    this.description = description;
    this.startTime = startTime;
    this.duration = duration;
    this.userId = userId;
  }

  public TimeEntry(long id, String description, Instant startTime, int duration, long userId) {
    this.description = description;
    this.startTime = startTime;
    this.duration = duration;
    this.userId = userId;
    this.setId(id);
  }

  public String getDescription() {
    return description;
  }

  public Instant getStartTime() {
    return startTime;
  }

  public Integer getDuration() {
    return duration;
  }

  public Long getUserId() {
    return userId;
  }

  public void update(String description, Instant startTime, Integer duration) {
    this.description = description;
    this.startTime = startTime;
    this.duration = duration;
  }

  public boolean isDuringPreferredTime() {
    return duringPreferredTime;
  }

  public void setDuringPreferredTime(boolean duringPreferredTime) {
    this.duringPreferredTime = duringPreferredTime;
  }

}
