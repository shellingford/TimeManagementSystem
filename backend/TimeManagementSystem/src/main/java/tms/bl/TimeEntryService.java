package tms.bl;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import tms.bl.error.BusinessLogicException;
import tms.bl.error.ValidationException;
import tms.config.user.UserContext;
import tms.model.entity.TimeEntry;
import tms.model.entity.UserSetting;
import tms.repository.TimeEntryRepository;

@Service
public class TimeEntryService {

  @Autowired
  private TimeEntryRepository timeEntryRepository;

  @Autowired
  private UserSettingService userSettingService;

  /**
   * Fetches all time entries for currently logged in user.
   *
   * @param userId
   *
   * @param user   currently logged in user
   * @return list of time entries
   */
  public Page<TimeEntry> getAll(Instant from, Instant to, Pageable pageable, Long userId) {
    if (userId != null && !isAdmin()) {
      throw new BusinessLogicException("no_access");
    }
    if (to.isBefore(from)) {
      throw new ValidationException("from_after_to", "from");
    }
    userId = userId == null ? UserContext.getUserId() : userId;
    Page<TimeEntry> timeEntries = timeEntryRepository.findAllBetween(userId, from, to, pageable);
    setPreferredTimes(timeEntries.getContent(), userId);
    return timeEntries;
  }

  /**
   * If TimeEntry is within UserSetting time it means it's during preferred time, so set value of duringPreferredTime to true.
   *
   * @param timeEntries list of time entries we are checking
   * @param userId      user's id for which we are getting UserSetting and checking entries
   */
  private void setPreferredTimes(List<TimeEntry> timeEntries, long userId) {
    UserSetting userSetting = userSettingService.getForUser(userId);
    if (userSetting == null) {
      return;
    }
    for (TimeEntry timeEntry : timeEntries) {
      LocalTime start = timeEntry.getStartTime().atZone(ZoneId.systemDefault()).toLocalTime();
      LocalTime end = start.plus(timeEntry.getDuration(), ChronoUnit.MINUTES);
      if ((start.equals(userSetting.getStartTime()) || start.isAfter(userSetting.getStartTime()))
          && (end.equals(userSetting.getEndTime()) || end.isBefore(userSetting.getEndTime()))) {
        timeEntry.setDuringPreferredTime(true);
      }
    }
  }

  /**
   * Creates new time entry for user with specified userId or currently logged in user. Only admin can create time entry for other user.
   *
   * @param description time entry description
   * @param from        time entry starting time
   * @param duration    time entry duration in minutes
   * @param userId      time entry's user id (optional)
   */
  public void create(String description, Instant from, Integer duration, Long userId) {
    if (userId != null && !isAdmin()) {
      throw new BusinessLogicException("no_access");
    }
    userId = userId == null ? UserContext.getUserId() : userId;
    timeEntryRepository.saveTimeEntry(duration, userId, description, from);
  }

  /**
   * Updates TimeEntry with specified id with specified values. Only owner of the TimeEntry or an admin can update it.
   *
   * @param id          time entry id
   * @param description new time entry description
   * @param from        new time entry from instant
   * @param duration    new time entry duration
   */
  public void update(Long id, String description, Instant from, Integer duration) {
    long userId = UserContext.getUserId();
    TimeEntry timeEntry = timeEntryRepository.findById(id).orElseThrow(() -> new BusinessLogicException("unknown_id"));
    if (timeEntry.getUserId() != userId && !isAdmin()) {
      throw new BusinessLogicException("not_owner");
    }
    timeEntry.update(description, from, duration);
    timeEntryRepository.save(timeEntry);
  }

  /**
   * Deletes TimeEntry with specified id. Only owner of the TimeEntry or an admin can delete it.
   *
   * @param id TimeEntry id
   */
  public void delete(Long id) {
    long userId = UserContext.getUserId();
    TimeEntry timeEntry = timeEntryRepository.findById(id).orElseThrow(() -> new BusinessLogicException("unknown_id"));
    if (timeEntry.getUserId() != userId && !isAdmin()) {
      throw new BusinessLogicException("not_owner");
    }
    timeEntryRepository.delete(timeEntry);
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
