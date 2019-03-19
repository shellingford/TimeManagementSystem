package tms.model.view;

import java.time.LocalTime;

import javax.validation.constraints.NotNull;

public class EditUserSettingVM {

  @NotNull(message = "missing")
  private LocalTime startTime;

  @NotNull(message = "missing")
  private LocalTime endTime;

  public LocalTime getStartTime() {
    return startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

}
