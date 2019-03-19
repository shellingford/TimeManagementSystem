package tms.model.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import tms.model.entity.Permission;

public class PermissionTest {

  @Test
  public void testConstructor() {
    String name = "test";
    int role = 2;
    Permission permission = new Permission(name, role);

    assertEquals(name, permission.getName());
    assertEquals(role, permission.getRole());
  }
}
