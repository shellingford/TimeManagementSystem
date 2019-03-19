package tms.model.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import tms.model.entity.Permission;
import tms.model.entity.User;

public class UserTest {

  @Test
  public void testConstructorWithUpdate() {
    String name = "test1";
    int role = 3;
    long id = 5;

    User user = new User(id, name, role);

    assertEquals(id, (long) user.getId());
    assertEquals(name, user.getName());
    assertEquals(role, user.getRole());
    assertEquals(null, user.getPassword());
    assertTrue(user.getPermissions().isEmpty());

    String password = "pass";
    name = "test1123";
    role = 31;
    user.updateWithNewPass(name, role, password);
    assertEquals(name, user.getName());
    assertEquals(role, user.getRole());
    assertEquals(password, user.getPassword());

    name = "test333";
    role = 22;
    user.update(name, role);
    assertEquals(name, user.getName());
    assertEquals(role, user.getRole());

    List<Permission> permissions = new ArrayList<>();
    permissions.add(new Permission("test"));
    user.setPermissions(permissions);
    assertEquals(permissions.size(), user.getPermissions().size());
    assertEquals(permissions.get(0).getName(), user.getPermissions().get(0));
  }
}
