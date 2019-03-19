package tms.model.view;

import java.util.List;

public class UserDataVM {

  private String name;
  private List<String> permissions;
  private String token;

  public UserDataVM(String name, List<String> permissions, String token) {
    this.name = name;
    this.permissions = permissions;
    this.token = token;
  }

  public String getName() {
    return name;
  }

  public List<String> getPermissions() {
    return permissions;
  }

  public String getToken() {
    return token;
  }

}
