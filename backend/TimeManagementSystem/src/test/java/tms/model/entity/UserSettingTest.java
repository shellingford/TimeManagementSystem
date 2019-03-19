package tms.model.entity;

import static org.junit.Assert.assertEquals;

import java.time.LocalTime;

import org.junit.Test;

import tms.model.entity.UserSetting;

public class UserSettingTest {

  @Test
  public void testConstructor() {
    Long id = 1L;
    LocalTime startTime = LocalTime.now();
    LocalTime endTime = LocalTime.now();

    UserSetting userSetting = new UserSetting(id, startTime, endTime);

    assertEquals(id, userSetting.getId());
    assertEquals(startTime, userSetting.getStartTime());
    assertEquals(endTime, userSetting.getEndTime());
  }

  @Test
  public void testUpdate() {
    Long id = 1L;
    LocalTime startTime = LocalTime.now();
    LocalTime endTime = LocalTime.now();

    UserSetting userSetting = new UserSetting(id, startTime, endTime);

    startTime = LocalTime.of(1, 1, 1);
    endTime = LocalTime.of(9, 9, 9);
    userSetting.update(startTime, endTime);

    assertEquals(startTime, userSetting.getStartTime());
    assertEquals(endTime, userSetting.getEndTime());
  }

}
