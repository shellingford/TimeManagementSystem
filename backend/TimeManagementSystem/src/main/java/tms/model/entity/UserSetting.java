package tms.model.entity;

import java.time.LocalTime;

import javax.persistence.Entity;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class UserSetting extends AbstractPersistable<Long> {

  private LocalTime startTime;
  private LocalTime endTime;
  private Long userId;

  protected UserSetting() {
  }

  public UserSetting(LocalTime startTime, LocalTime endTime, long userId) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.userId = userId;
  }

  public UserSetting(long id, LocalTime startTime, LocalTime endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.setId(id);
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public Long getUserId() {
    return userId;
  }

  public void update(LocalTime startTime, LocalTime endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }
}
