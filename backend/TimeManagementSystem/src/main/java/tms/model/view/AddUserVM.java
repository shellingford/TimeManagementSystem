package tms.model.view;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AddUserVM {

  @NotNull(message = "missing")
  @Size(min = 2, max = 50, message = "min_max")
  private String name;

  @NotNull(message = "missing")
  private int role;

  public String getName() {
    return name;
  }

  public int getRole() {
    return role;
  }
}
