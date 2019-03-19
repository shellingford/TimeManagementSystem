package tms.model.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import tms.model.entity.UserSession;

public class UserSessionTest {

  @Test
  public void testConstructor() {
    String guid = "asrs0a9fk23";
    Long userId = 4L;

    UserSession userSession = new UserSession(guid, userId);

    assertEquals(guid, userSession.getGuid());
    assertEquals(userId, userSession.getUserId());
  }
}
