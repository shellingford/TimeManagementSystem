package tms.model.view;

import java.time.LocalTime;

public class UserSettingVM {

  private long id;
  private LocalTime startTime;
  private LocalTime endTime;

  public UserSettingVM(LocalTime startTime, LocalTime endTime, long id) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.id = id;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public long getId() {
    return id;
  }
}
