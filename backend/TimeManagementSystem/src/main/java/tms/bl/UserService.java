package tms.bl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import tms.bl.error.BusinessLogicException;
import tms.bl.error.ValidationException;
import tms.config.user.UserContext;
import tms.model.entity.User;
import tms.repository.PermissionRepository;
import tms.repository.UserRepository;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PermissionRepository permissionRepository;

  @Autowired
  private PasswordService passwordService;

  /**
   * Gets all users. Only user's with manager permission can use it.
   *
   * @param pageable
   * @return page of user data
   */
  public Page<User> getAll(Pageable pageable) {
    if (!UserContext.getPermissions().contains("manager")) {
      throw new BusinessLogicException("no_access");
    }
    return userRepository.findAll(pageable);
  }

  /**
   * Saves user.
   *
   * @param user
   */
  public void save(User user) {
    userRepository.save(user.getName(), user.getRole(), user.getPassword());
  }

  /**
   * Finds user with the specified name, loads user's permissions and returns user object.
   *
   * @param name user name
   * @return user object
   */
  public User getUserByNameWithPermissions(String name) {
    User user = userRepository.findByName(name);
    user = loadUserPermissions(user);
    return user;
  }

  /**
   * Loads user's permissions from permission table by user's role.
   *
   * @param user
   * @return user data with permissions
   */
  private User loadUserPermissions(User user) {
    if (user != null) {
      user.setPermissions(permissionRepository.findByRole(user.getRole()));
    }
    return user;
  }

  /**
   * Loads data about currently logged in user with permissions.
   *
   * @return user data with permissions for currently logged in user
   */
  public User getCurrentUserWithPermissions() {
    User user = userRepository.findById(UserContext.getUserId()).orElseThrow(() -> new BusinessLogicException("unknown_user_id"));
    user = loadUserPermissions(user);
    return user;
  }

  /**
   * Checks if user exists in db with specified name, if yes then return true, false otherwise.
   *
   * @param name user name
   * @return true if user exists, false otherwise
   */
  public boolean userExistsWithName(String name) {
    return userRepository.countByName(name) > 0;
  }

  /**
   * Deletes user specified by id, but checks first if that user is not currently logged in one which then triggers new BusinessLogicException.
   *
   * @param user currently logged in user
   * @param id   id of user that we want to delete
   */
  public void delete(Long id) {
    if (!UserContext.getPermissions().contains("manager")) {
      throw new BusinessLogicException("no_access");
    }
    if (UserContext.getUserId() == id) {
      throw new BusinessLogicException("cannot_delete_yourself");
    }
    userRepository.deleteById(id);
  }

  /**
   * Updates user data. If password is changed then it validates it before doing the change.
   *
   * @param id              id of user that will be updated
   * @param name            user name
   * @param role            user role
   * @param password        user's new password
   * @param confirmPassword user's new repeated password (to avoid typo mistakes)
   */
  public void update(Long id, String name, Integer role, String password, String confirmPassword) {
    if (!UserContext.getPermissions().contains("manager")) {
      throw new BusinessLogicException("no_access");
    }

    if (userExistsWithName(name)) {
      throw new ValidationException("name_already_exists", "name");
    }

    User user = userRepository.findById(id).orElseThrow(() -> new BusinessLogicException("unknown_user_id"));
    if (password != null) {
      passwordService.passwordValidation(password, confirmPassword, name);
      user.updateWithNewPass(name, role, passwordService.getPasswordHash(password));
    } else {
      user.update(name, role);
    }
    userRepository.save(user);
  }

  /**
   * Finds user with the specified id, loads user's permissions and returns user object.
   *
   * @param name user name
   * @return user object
   */
  public User getUserByIdWithPermissions(long id) {
    User user = userRepository.findById(id).orElseThrow(() -> new BusinessLogicException("unknown_user_id"));
    user = loadUserPermissions(user);
    return user;
  }

  /**
   * Creates new User with specified name and role.
   *
   * @param name user name
   * @param role user role
   */
  public void create(String name, int role) {
    if (!UserContext.getPermissions().contains("manager")) {
      throw new BusinessLogicException("no_access");
    }
    userRepository.save(name, role, passwordService.generateRandomPassword());
  }

  /**
   * Gets list of users for admin dropdown.
   *
   * @return list of all users
   */
  public List<User> getAllForDropdown() {
    if (!UserContext.getPermissions().contains("admin")) {
      throw new BusinessLogicException("no_access");
    }
    return userRepository.findAll();
  }
}
