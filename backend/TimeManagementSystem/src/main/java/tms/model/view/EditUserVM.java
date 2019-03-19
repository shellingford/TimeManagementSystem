package tms.model.view;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class EditUserVM {

  @NotNull(message = "missing")
  @Size(min = 3, max = 100, message = "min_max")
  private String name;

  @NotNull(message = "missing")
  private Integer role;

  private String password;

  private String confirmPassword;

  public String getName() {
    return name;
  }

  public Integer getRole() {
    return role;
  }

  public String getPassword() {
    return password;
  }

  public String getConfirmPassword() {
    return confirmPassword;
  }
}
