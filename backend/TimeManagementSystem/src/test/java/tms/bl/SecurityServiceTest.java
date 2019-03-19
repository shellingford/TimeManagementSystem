package tms.bl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import tms.config.user.UserContext;
import tms.model.entity.User;
import tms.model.entity.UserSession;
import tms.repository.UserSessionRepository;

public class SecurityServiceTest {

  @Mock
  private UserService userService;

  @Mock
  private PasswordService passwordService;

  @Mock
  private UserSessionRepository userSessionRepository;

  @InjectMocks
  private SecurityService securityService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    UserContext.clear();
  }

  @Test
  public void register() {
    String name = "test";
    String passwordHash = "lsfds9aigfk3";
    String pass = "testPass";

    Mockito.when(userService.userExistsWithName(name)).thenReturn(false);
    Mockito.when(passwordService.getPasswordHash(pass)).thenReturn(passwordHash);

    securityService.register(name, pass, pass);

    Mockito.verify(userService, times(1)).save(Mockito.any(User.class));
  }

  @Test
  public void login() {
    String username = "test";
    String password = "pass";
    long userId = 2;
    User user = new User(userId, username, password);

    Mockito.when(userService.getUserByNameWithPermissions(username)).thenReturn(user);
    Mockito.when(passwordService.passwordsMatch(password, password)).thenReturn(true);

    String token = securityService.login(username, password);

    assertNotNull(token);
    assertEquals(user.getId(), UserContext.getUserId());
    assertEquals(user.getPermissions(), UserContext.getPermissions());
    Mockito.verify(userSessionRepository, times(1)).save(Mockito.any(UserSession.class));
  }

  @Test
  public void isValidAuthToken() {
    String username = "test";
    String password = "pass";
    long userId = 2;
    User user = new User(userId, username, password);
    String authToken = "sdlaifd98323";

    UserSession session = new UserSession(authToken, userId);
    Mockito.when(userSessionRepository.findByGuid(authToken)).thenReturn(session);
    Mockito.when(userService.getUserByIdWithPermissions(userId)).thenReturn(user);

    assertTrue(securityService.isValidAuthToken(authToken));
  }

}
