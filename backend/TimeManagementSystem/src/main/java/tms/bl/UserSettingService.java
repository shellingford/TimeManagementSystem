package tms.bl;

import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tms.bl.error.BusinessLogicException;
import tms.bl.error.ValidationException;
import tms.config.user.UserContext;
import tms.model.entity.UserSetting;
import tms.repository.UserSettingRepository;

@Service
public class UserSettingService {

  @Autowired
  private UserSettingRepository userSettingRepository;

  /**
   * Gets UserSetting for user specified by userId or currently logged in user. Only admin can get UserSetting for other users.
   *
   * @param userId user's id (optional)
   * @return UserSetting for user specified by userId or currently logged in one
   */
  public UserSetting getForUserWithAuth(Long userId) {
    if (userId != null && !isAdmin()) {
      throw new BusinessLogicException("no_access");
    }
    userId = userId == null ? UserContext.getUserId() : userId;
    return userSettingRepository.findByUserId(userId);
  }

  /**
   * Get UserSetting for user specified by userId.
   *
   * @param userId user's id
   * @return user setting
   */
  public UserSetting getForUser(Long userId) {
    return userSettingRepository.findByUserId(userId);
  }

  /**
   * Creates new UserSetting for user with specified userId or currently logged in user. Only admin can create UserSetting for other user.
   *
   * @param endTime   UserSetting end time
   * @param startTime UserSetting start time
   * @param userId    user's id (optional)
   */
  public void create(LocalTime endTime, LocalTime startTime, Long userId) {
    if (userId != null && !isAdmin()) {
      throw new BusinessLogicException("no_access");
    }
    userId = userId == null ? UserContext.getUserId() : userId;
    UserSetting userSetting = userSettingRepository.findByUserId(userId);
    if (userSetting != null) {
      throw new BusinessLogicException("duplicate_setting");
    }
    if (endTime.isBefore(startTime)) {
      throw new ValidationException("start_after_end", "startTime");
    }
    userSettingRepository.saveSetting(userId, startTime, endTime);
  }

  /**
   * Updates UserSetting with specified id with specified values. Only owner of the UserSetting or an admin can update it.
   *
   * @param id        UserSetting id
   * @param startTime UserSetting new start time
   * @param endTime   UserSetting new end time
   */
  public void update(Long id, LocalTime startTime, LocalTime endTime) {
    long userId = UserContext.getUserId();
    UserSetting userSetting = userSettingRepository.findById(id).orElseThrow(() -> new BusinessLogicException("unknown_id"));
    if (userSetting.getUserId() != userId && !isAdmin()) {
      throw new BusinessLogicException("not_owner");
    }
    if (endTime.isBefore(startTime)) {
      throw new ValidationException("start_after_end", "startTime");
    }
    userSetting.update(startTime, endTime);
    userSettingRepository.save(userSetting);
  }

  /**
   * Deletes UserSetting with specified id. Only owner of the UserSetting or an admin can delete it.
   *
   * @param id UserSetting id
   */
  public void delete(Long id) {
    UserSetting userSetting = userSettingRepository.findById(id).orElseThrow(() -> new BusinessLogicException("unknown_id"));
    long userId = UserContext.getUserId();
    if (userSetting.getUserId() != userId && !isAdmin()) {
      throw new BusinessLogicException("not_owner");
    }
    userSettingRepository.delete(userSetting);
  }

  /**
   * Checks if currently logged in user is admin.
   *
   * @return true if user is admin, false otherwise
   */
  private boolean isAdmin() {
    return UserContext.getPermissions().contains("admin");
  }

}
