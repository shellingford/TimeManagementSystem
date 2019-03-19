package tms.model.view;

import java.time.Instant;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AddTimeEntryVM {

  @NotNull(message = "missing")
  @Size(min = 2, max = 500, message = "min_max")
  private String description;

  @NotNull(message = "missing")
  private Long from;

  @NotNull(message = "missing")
  @Min(value = 1, message = "min")
  private Integer duration;

  private Long userId;

  public String getDescription() {
    return description;
  }

  public Instant getFrom() {
    return Instant.ofEpochSecond(from);
  }

  public Integer getDuration() {
    return duration;
  }

  public Long getUserId() {
    return userId;
  }
}
