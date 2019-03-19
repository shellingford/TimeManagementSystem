package tms.model.view;

import java.time.LocalTime;

import javax.validation.constraints.NotNull;

public class AddUserSettingVM {

  @NotNull(message = "missing")
  private LocalTime startTime;

  @NotNull(message = "missing")
  private LocalTime endTime;

  private Long userId;

  public LocalTime getStartTime() {
    return startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public Long getUserId() {
    return userId;
  }

}
