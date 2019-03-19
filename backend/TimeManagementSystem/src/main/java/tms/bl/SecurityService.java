package tms.bl;

import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import tms.bl.error.ValidationException;
import tms.config.user.UserContext;
import tms.model.entity.User;
import tms.model.entity.UserSession;
import tms.repository.UserSessionRepository;

@Service
public class SecurityService {

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordService passwordService;

  @Autowired
  private UserSessionRepository userSessionRepository;

  /**
   * Checks if user is already logged in (user could call endpoint directly, should never happen in web app), if user name already exists in db and if
   * password is complex enough. If any of these is true then throw corresponding exception, otherwise create new user which can be used to login later.
   *
   * @param userName       name
   * @param password       password
   * @param repeatPassword repeated password (to avoid typo mistakes)
   * @param user           currently logged in user
   */
  public void register(String userName, String password, String repeatPassword) {
    if (userService.userExistsWithName(userName)) {
      throw new ValidationException("name_already_exists", "name");
    }
    passwordService.passwordValidation(password, repeatPassword, userName);

    userService.save(new User(userName, passwordService.getPasswordHash(password)));
  }

  /**
   * Checks if user with specified username exists and if password is valid, if yes then logs in user and sets UserContext data. If user can login then
   * it returns authentication token that will be used by frontend.
   *
   * @param username
   * @param password
   * @return authentication token
   */
  public String login(String username, String password) {
    User user = userService.getUserByNameWithPermissions(username);
    if (user == null) {
      throw new ValidationException("login_failed", "password");
    }

    if (!passwordService.passwordsMatch(password, user.getPassword())) {
      throw new ValidationException("login_failed", "password");
    }

    UserSession userSession = new UserSession(UUID.randomUUID().toString(), user.getId());
    userSessionRepository.save(userSession);
    UserContext.setData(user.getId(), user.getPermissions());
    return userSession.getGuid();
  }

  /**
   * Checks if authentication token is valid (has corresponding UserSession). If it is then it loads user data into UserContext.
   *
   * @param authToken user authentication token
   * @return true if it is valid, false otherwise
   */
  public boolean isValidAuthToken(String authToken) {
    UserSession userSession = userSessionRepository.findByGuid(authToken);
    if (userSession != null) {
      User user = userService.getUserByIdWithPermissions(userSession.getUserId());
      UserContext.setData(user.getId(), user.getPermissions());
    }
    return userSession != null;
  }

  /**
   * Logs out user, clears user session and UserContext.
   *
   * @throws ServletException
   */
  public void logout() throws ServletException {
    HttpServletRequest currentRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    currentRequest.logout();
    userSessionRepository.deleteByUserId(UserContext.getUserId());
    UserContext.clear();
  }
}
