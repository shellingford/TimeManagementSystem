package tms.config.user;

import java.util.List;

public class UserContext {

  private static final ThreadLocal<LoggedInUser> CONTEXT = new ThreadLocal<>();

  public static void setData(long userId, List<String> permissions) {
    CONTEXT.set(new LoggedInUser(userId, permissions));
  }

  public static Long getUserId() {
    return CONTEXT.get() != null ? CONTEXT.get().getUserId() : null;
  }

  public static List<String> getPermissions() {
    return CONTEXT.get() != null ? CONTEXT.get().getPermissions() : null;
  }

  public static void clear() {
    CONTEXT.remove();
  }
}
