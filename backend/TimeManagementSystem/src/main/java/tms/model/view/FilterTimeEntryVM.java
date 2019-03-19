package tms.model.view;

import java.time.Instant;

public class FilterTimeEntryVM {

  private String description;
  private Long from;
  private Long to;
  private Integer duration;
  private Long id;
  private Long userId;

  public String getDescription() {
    return description;
  }

  public Instant getFrom() {
    return from != null ? Instant.ofEpochSecond(from) : null;
  }

  public Integer getDuration() {
    return duration;
  }

  public Long getId() {
    return id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFrom(Long from) {
    this.from = from;
  }

  public Instant getTo() {
    return to != null ? Instant.ofEpochSecond(to) : null;
  }

  public void setTo(Long to) {
    this.to = to;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

}
