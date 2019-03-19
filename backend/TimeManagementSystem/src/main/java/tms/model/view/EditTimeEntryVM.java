package tms.model.view;

import java.time.Instant;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class EditTimeEntryVM {

  @NotNull(message = "missing")
  @Size(min = 2, max = 500, message = "min_max")
  private String description;

  @NotNull(message = "missing")
  private Instant from;

  @NotNull(message = "missing")
  @Min(message = "min", value = 1)
  private Integer duration;

  public String getDescription() {
    return description;
  }

  public Instant getFrom() {
    return from;
  }

  public Integer getDuration() {
    return duration;
  }
}
