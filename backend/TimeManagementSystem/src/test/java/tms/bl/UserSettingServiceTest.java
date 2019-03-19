package tms.bl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import tms.bl.error.BusinessLogicException;
import tms.bl.error.ValidationException;
import tms.config.user.UserContext;
import tms.model.entity.UserSetting;
import tms.repository.UserSettingRepository;

public class UserSettingServiceTest {

  @Mock
  private UserSettingRepository userSettingRepository;

  @InjectMocks
  private UserSettingService userSettingService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    UserContext.clear();
  }

  private void setupAdmin(long userId) {
    List<String> permissions = new ArrayList<>();
    permissions.add("admin");
    UserContext.setData(userId, permissions);
  }

  private void setupUser(long userId) {
    List<String> permissions = new ArrayList<>();
    permissions.add("user");
    UserContext.setData(userId, permissions);
  }

  @Test
  public void getForUserWithAuth() {
    long userId = 1;
    setupUser(userId);
    UserSetting setting = new UserSetting(1L, LocalTime.now(), LocalTime.now());
    Mockito.when(userSettingRepository.findByUserId(userId)).thenReturn(setting);

    UserSetting userSetting = userSettingService.getForUserWithAuth(null);

    assertEquals(setting.getStartTime(), userSetting.getStartTime());
    assertEquals(setting.getEndTime(), userSetting.getEndTime());
    assertEquals(setting.getId(), userSetting.getId());

    boolean thrownException = false;
    try {
      userSettingService.getForUserWithAuth(userId);
    } catch (BusinessLogicException e) {
      thrownException = true;
    }
    assertTrue(thrownException);
  }

  @Test
  public void getForUserWithAuthAdmin() {
    long userId = 1;
    setupAdmin(3L);
    UserSetting setting = new UserSetting(1L, LocalTime.now(), LocalTime.now());
    Mockito.when(userSettingRepository.findByUserId(userId)).thenReturn(setting);

    UserSetting userSetting = userSettingService.getForUserWithAuth(userId);

    assertEquals(setting.getStartTime(), userSetting.getStartTime());
    assertEquals(setting.getEndTime(), userSetting.getEndTime());
    assertEquals(setting.getId(), userSetting.getId());
  }

  @Test
  public void getForUser() {
    long userId = 1;
    setupUser(userId);
    UserSetting setting = new UserSetting(1L, LocalTime.now(), LocalTime.now());
    Mockito.when(userSettingRepository.findByUserId(userId)).thenReturn(setting);

    UserSetting userSetting = userSettingService.getForUser(userId);

    assertEquals(setting.getStartTime(), userSetting.getStartTime());
    assertEquals(setting.getEndTime(), userSetting.getEndTime());
    assertEquals(setting.getId(), userSetting.getId());
  }

  @Test
  public void create() {
    long userId = 1;
    setupUser(userId);
    LocalTime startTime = LocalTime.now();
    LocalTime endTime = LocalTime.now();

    userSettingService.create(endTime, startTime, null);
    Mockito.verify(userSettingRepository, times(1)).saveSetting(userId, startTime, endTime);
  }

  @Test(expected = ValidationException.class)
  public void createWrongTimes() {
    long userId = 1;
    setupUser(userId);
    LocalTime startTime = LocalTime.of(2, 1);
    LocalTime endTime = LocalTime.of(1, 1);

    userSettingService.create(endTime, startTime, null);
  }

  @Test(expected = BusinessLogicException.class)
  public void createDuplicate() {
    long userId = 1;
    setupUser(userId);
    LocalTime startTime = LocalTime.of(2, 1);
    LocalTime endTime = LocalTime.of(1, 1);

    UserSetting setting = new UserSetting(1L, LocalTime.now(), LocalTime.now());
    Mockito.when(userSettingRepository.findByUserId(userId)).thenReturn(setting);

    userSettingService.create(endTime, startTime, null);
  }

  @Test(expected = BusinessLogicException.class)
  public void createNotAdmin() {
    long userId = 1;
    setupUser(userId);
    LocalTime startTime = LocalTime.of(2, 1);
    LocalTime endTime = LocalTime.of(1, 1);

    userSettingService.create(endTime, startTime, 4L);
  }

  @Test
  public void createAdmin() {
    setupAdmin(4L);
    long userId = 1;
    LocalTime startTime = LocalTime.of(1, 1);
    LocalTime endTime = LocalTime.of(2, 1);

    userSettingService.create(endTime, startTime, userId);
    Mockito.verify(userSettingRepository, times(1)).saveSetting(userId, startTime, endTime);
  }

  @Test
  public void update() {
    long userId = 1;
    setupUser(userId);
    LocalTime startTime = LocalTime.now();
    LocalTime endTime = LocalTime.now();
    long settingId = 1;

    UserSetting userSetting = new UserSetting(startTime, endTime, userId);
    Mockito.when(userSettingRepository.findById(userId)).thenReturn(Optional.of(userSetting));

    LocalTime startTime2 = LocalTime.of(1, 1);
    LocalTime endTime2 = LocalTime.of(2, 1);
    userSettingService.update(settingId, startTime2, endTime2);
    userSetting.update(startTime2, endTime2);

    Mockito.verify(userSettingRepository, times(1)).save(userSetting);
  }

  @Test(expected = ValidationException.class)
  public void updateWrongTimes() {
    long userId = 1;
    setupUser(userId);
    LocalTime startTime = LocalTime.of(2, 1);
    LocalTime endTime = LocalTime.of(1, 1);
    long settingId = 1;

    UserSetting userSetting = new UserSetting(startTime, endTime, userId);
    Mockito.when(userSettingRepository.findById(settingId)).thenReturn(Optional.of(userSetting));

    userSettingService.update(settingId, startTime, endTime);
  }

  @Test(expected = BusinessLogicException.class)
  public void updateNotAdmin() {
    long userId = 1;
    setupUser(userId);
    LocalTime startTime = LocalTime.of(2, 1);
    LocalTime endTime = LocalTime.of(1, 1);
    long settingId = 1;

    UserSetting userSetting = new UserSetting(startTime, endTime, 4L);
    Mockito.when(userSettingRepository.findById(settingId)).thenReturn(Optional.of(userSetting));

    userSettingService.update(settingId, startTime, endTime);
  }

  @Test
  public void updateAdmin() {
    long userId = 5;
    setupAdmin(4L);
    LocalTime startTime = LocalTime.of(1, 1);
    LocalTime endTime = LocalTime.of(2, 1);
    long settingId = 1;

    UserSetting userSetting = new UserSetting(startTime, endTime, userId);
    Mockito.when(userSettingRepository.findById(settingId)).thenReturn(Optional.of(userSetting));

    LocalTime startTime2 = LocalTime.of(1, 1);
    LocalTime endTime2 = LocalTime.of(2, 1);
    userSettingService.update(settingId, startTime2, endTime2);
    userSetting.update(startTime2, endTime2);

    Mockito.verify(userSettingRepository, times(1)).save(userSetting);
  }

  @Test
  public void delete() {
    long userId = 1;
    setupUser(userId);
    LocalTime startTime = LocalTime.now();
    LocalTime endTime = LocalTime.now();
    long settingId = 1;

    UserSetting userSetting = new UserSetting(startTime, endTime, userId);
    Mockito.when(userSettingRepository.findById(userId)).thenReturn(Optional.of(userSetting));

    userSettingService.delete(settingId);

    Mockito.verify(userSettingRepository, times(1)).delete(userSetting);
  }

  @Test(expected = BusinessLogicException.class)
  public void deleteNotAdmin() {
    long userId = 1;
    setupUser(userId);
    LocalTime startTime = LocalTime.of(2, 1);
    LocalTime endTime = LocalTime.of(1, 1);
    long settingId = 1;

    UserSetting userSetting = new UserSetting(startTime, endTime, 4L);
    Mockito.when(userSettingRepository.findById(settingId)).thenReturn(Optional.of(userSetting));

    userSettingService.delete(settingId);
  }

  @Test
  public void deleteAdmin() {
    long userId = 5;
    setupAdmin(4L);
    LocalTime startTime = LocalTime.of(1, 1);
    LocalTime endTime = LocalTime.of(2, 1);
    long settingId = 1;

    UserSetting userSetting = new UserSetting(startTime, endTime, userId);
    Mockito.when(userSettingRepository.findById(settingId)).thenReturn(Optional.of(userSetting));

    userSettingService.delete(settingId);

    Mockito.verify(userSettingRepository, times(1)).delete(userSetting);
  }
}
