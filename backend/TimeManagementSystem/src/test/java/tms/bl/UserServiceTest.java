package tms.bl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import tms.bl.error.BusinessLogicException;
import tms.config.user.UserContext;
import tms.model.entity.Permission;
import tms.model.entity.User;
import tms.repository.PermissionRepository;
import tms.repository.UserRepository;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PermissionRepository permissionRepository;

  @Mock
  private PasswordService passwordService;

  @InjectMocks
  private UserService userService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    UserContext.clear();
  }

  private void setupManager(long userId) {
    List<String> permissions = new ArrayList<>();
    permissions.add("manager");
    UserContext.setData(userId, permissions);
  }

  private void setupUser(long userId) {
    List<String> permissions = new ArrayList<>();
    permissions.add("user");
    UserContext.setData(userId, permissions);
  }

  private void setupAdmin(long userId) {
    List<String> permissions = new ArrayList<>();
    permissions.add("admin");
    UserContext.setData(userId, permissions);
  }

  @Test(expected = BusinessLogicException.class)
  public void getAllNonManager() {
    UserContext.setData(1L, new ArrayList<>());
    userService.getAll(null);
  }

  @Test
  public void getAll() {
    long userId = 1;
    setupManager(userId);
    Pageable pageable = Mockito.mock(Pageable.class);

    List<User> userList = new ArrayList<>();
    User user = new User(userId, "test", 1);
    userList.add(user);
    Page<User> page = new PageImpl<>(userList);
    Mockito.when(userRepository.findAll(pageable)).thenReturn(page);

    Page<User> userPage = userService.getAll(pageable);
    assertEquals(1, userPage.getContent().size());
    assertEquals(user, userPage.getContent().get(0));
  }

  @Test
  public void save() {
    User user = new User(1L, "test", 1);
    userService.save(user);
    Mockito.verify(userRepository, times(1)).save(user.getName(), user.getRole(), user.getPassword());
  }

  @Test
  public void getUserByNameWithPermissions() {
    String name = "test";
    int role = 2;
    User user = new User(1L, name, role);

    Mockito.when(userRepository.findByName(name)).thenReturn(user);

    List<Permission> permissions = new ArrayList<>();
    permissions.add(new Permission("manager", role));
    user.setPermissions(permissions);
    Mockito.when(permissionRepository.findByRole(role)).thenReturn(permissions);

    User retUser = userService.getUserByNameWithPermissions(name);

    assertEquals(user.getId(), retUser.getId());
    assertEquals(user.getName(), retUser.getName());
    assertEquals(user.getRole(), retUser.getRole());
    assertEquals(user.getPermissions(), retUser.getPermissions());
  }

  @Test
  public void getCurrentUserWithPermissions() {
    String name = "test";
    int role = 2;
    User user = new User(1L, name, role);

    Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

    List<Permission> permissions = new ArrayList<>();
    permissions.add(new Permission("manager", role));
    user.setPermissions(permissions);
    UserContext.setData(user.getId(), user.getPermissions());
    Mockito.when(permissionRepository.findByRole(role)).thenReturn(permissions);

    User retUser = userService.getCurrentUserWithPermissions();

    assertEquals(user.getId(), retUser.getId());
    assertEquals(user.getName(), retUser.getName());
    assertEquals(user.getRole(), retUser.getRole());
    assertEquals(user.getPermissions(), retUser.getPermissions());
  }

  @Test
  public void userExistsWithName() {
    String name = "test";
    Mockito.when(userRepository.countByName(name)).thenReturn(1L);
    assertTrue(userService.userExistsWithName(name));
    Mockito.when(userRepository.countByName(name)).thenReturn(0L);
    assertFalse(userService.userExistsWithName(name));
  }

  @Test
  public void delete() {
    long userId = 1;
    setupManager(2L);

    userService.delete(userId);
    Mockito.verify(userRepository, times(1)).deleteById(userId);
  }

  @Test(expected = BusinessLogicException.class)
  public void deleteSelf() {
    long userId = 1;
    setupManager(userId);
    userService.delete(userId);
  }

  @Test(expected = BusinessLogicException.class)
  public void deleteNonManager() {
    long userId = 1;
    setupUser(2L);
    userService.delete(userId);
  }

  @Test
  public void update() {
    long userId = 1;
    setupManager(userId);

    String name = "test";
    int role = 2;
    User user = new User(userId, "old_name", 1);
    Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

    userService.update(userId, name, role, null, null);
    user.update(name, role);
    Mockito.verify(userRepository, times(1)).save(user);
  }

  @Test
  public void getUserByIdWithPermissions() {
    String name = "test";
    int role = 2;
    User user = new User(1L, name, role);

    Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

    List<Permission> permissions = new ArrayList<>();
    permissions.add(new Permission("manager", role));
    user.setPermissions(permissions);
    Mockito.when(permissionRepository.findByRole(role)).thenReturn(permissions);

    User retUser = userService.getUserByIdWithPermissions(user.getId());

    assertEquals(user.getId(), retUser.getId());
    assertEquals(user.getName(), retUser.getName());
    assertEquals(user.getRole(), retUser.getRole());
    assertEquals(user.getPermissions(), retUser.getPermissions());
  }

  @Test
  public void create() {
    setupManager(2L);
    String name = "test";
    int role = 1;

    userService.create(name, role);
    Mockito.verify(userRepository, times(1)).save(name, role, null);
  }

  @Test(expected = BusinessLogicException.class)
  public void createNonManager() {
    setupUser(2L);
    String name = "test";
    int role = 1;
    userService.create(name, role);
  }

  @Test
  public void getAllForDropdown() {
    setupAdmin(2L);
    List<User> users = new ArrayList<>();
    users.add(new User(1L, "test", 2));
    users.add(new User(2L, "test2", 3));
    Mockito.when(userRepository.findAll()).thenReturn(users);

    List<User> retUsers = userService.getAllForDropdown();
    assertEquals(users, retUsers);
  }

  @Test(expected = BusinessLogicException.class)
  public void getAllForDropdownNonAdmin() {
    setupManager(2L);
    userService.getAllForDropdown();
  }

}
