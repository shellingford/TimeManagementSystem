package tms.bl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import tms.bl.ViewModelService;
import tms.model.entity.Permission;
import tms.model.entity.TimeEntry;
import tms.model.entity.User;
import tms.model.entity.UserSetting;
import tms.model.view.TimeEntryVM;
import tms.model.view.UserDataVM;
import tms.model.view.UserDropdownVM;
import tms.model.view.UserSettingVM;
import tms.model.view.UserVM;

public class ViewModelServiceTest {

  @InjectMocks
  private ViewModelService viewModelService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testConvertTimeEntries() {
    List<TimeEntry> listEntries = new ArrayList<>();
    listEntries.add(new TimeEntry(1L, "descr", Instant.now(), 5, 3L));
    listEntries.add(new TimeEntry(2L, "descr", Instant.now(), 43, 5L));
    Page<TimeEntry> pageEntries = new PageImpl<>(listEntries);

    Page<TimeEntryVM> vmPage = viewModelService.convertTimeEntries(pageEntries);

    assertEquals(listEntries.size(), vmPage.getContent().size());
    vmPage.forEach(vm -> {
      listEntries.forEach(entity -> {
        if (entity.getId() == vm.getId()) {
          assertEquals(entity.getDescription(), vm.getDescription());
          assertEquals((int) entity.getDuration(), vm.getDuration());
          assertEquals(entity.getStartTime(), vm.getFrom());
          assertEquals(entity.isDuringPreferredTime(), vm.isDuringPreferredTime());
        }
      });
    });
  }

  @Test
  public void testConvertUserSettings() {
    UserSetting userSetting = new UserSetting(1L, LocalTime.now(), LocalTime.now());
    UserSettingVM vm = viewModelService.convertUserSettings(userSetting);

    assertEquals(userSetting.getStartTime(), vm.getStartTime());
    assertEquals(userSetting.getEndTime(), vm.getEndTime());
    assertEquals((long) userSetting.getId(), vm.getId());
  }

  @Test
  public void testConvertUserData() {
    User user = new User(1L, "test1", 1);
    String token = "test_token";

    List<Permission> permissions = new ArrayList<>();
    permissions.add(new Permission("test"));
    user.setPermissions(permissions);
    UserDataVM vm = viewModelService.convertUserData(user, token);

    assertEquals(user.getName(), vm.getName());
    assertEquals(token, vm.getToken());

    List<String> permissionNames = vm.getPermissions();
    assertEquals(permissions.size(), permissionNames.size());
    assertEquals(permissions.get(0).getName(), permissionNames.get(0));
  }

  @Test
  public void testConvertUsers() {
    List<User> listUsers = new ArrayList<>();
    listUsers.add(new User(1L, "test1", 1));
    listUsers.add(new User(2L, "test2", 2));
    Page<User> pageUsers = new PageImpl<>(listUsers);

    Page<UserVM> vmPage = viewModelService.convertUsers(pageUsers);

    assertEquals(listUsers.size(), vmPage.getContent().size());
    vmPage.forEach(vm -> {
      listUsers.forEach(entity -> {
        if (entity.getId() == vm.getId()) {
          assertEquals(entity.getName(), vm.getName());
          assertEquals((long) entity.getId(), vm.getId());
          assertEquals(entity.getRole(), vm.getRole());
        }
      });
    });
  }

  @Test
  public void testConvertUsersForDropdown() {
    List<User> listUsers = new ArrayList<>();
    listUsers.add(new User(1L, "test1", 1));
    listUsers.add(new User(2L, "test2", 2));

    List<UserDropdownVM> emptyList = viewModelService.convertUsersForDropdown(null);
    assertTrue(emptyList.isEmpty());

    List<UserDropdownVM> vmList = viewModelService.convertUsersForDropdown(listUsers);

    assertEquals(listUsers.size(), vmList.size());
    vmList.forEach(vm -> {
      listUsers.forEach(entity -> {
        if (entity.getName() == vm.getText()) {
          assertEquals(entity.getRole(), vm.getValue());
        }
      });
    });

  }
}
