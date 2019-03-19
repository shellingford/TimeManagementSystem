package tms.config.user;

import java.util.List;

public class LoggedInUser {
  private long userId;
  private List<String> permissions;

  public LoggedInUser(long userId, List<String> permissions) {
    this.userId = userId;
    this.permissions = permissions;
  }

  public long getUserId() {
    return userId;
  }

  public List<String> getPermissions() {
    return permissions;
  }

}
