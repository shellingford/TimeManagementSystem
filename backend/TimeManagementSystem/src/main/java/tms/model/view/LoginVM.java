package tms.model.view;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LoginVM {

  @NotNull(message = "missing")
  @Size(min = 3, max = 100, message = "min_max")
  private String name;

  @NotNull(message = "missing")
  @Size(min = 10, max = 50, message = "min_max")
  private String password;

  public String getName() {
    return name;
  }

  public String getPassword() {
    return password;
  }
}
