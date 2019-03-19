package tms.bl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import tms.model.entity.TimeEntry;
import tms.model.entity.User;
import tms.model.entity.UserSetting;
import tms.model.view.TimeEntryVM;
import tms.model.view.UserDataVM;
import tms.model.view.UserDropdownVM;
import tms.model.view.UserSettingVM;
import tms.model.view.UserVM;

/**
 * Class for converting from entity objects to view model objects.
 */
@Service
public class ViewModelService {

  public Page<TimeEntryVM> convertTimeEntries(Page<TimeEntry> timeEntries) {
    return timeEntries.map(timeEntry -> new TimeEntryVM(timeEntry.getDescription(), timeEntry.getStartTime(), timeEntry.getDuration(), timeEntry.getId(),
        timeEntry.isDuringPreferredTime()));
  }

  public UserSettingVM convertUserSettings(UserSetting userSetting) {
    return userSetting != null ? new UserSettingVM(userSetting.getStartTime(), userSetting.getEndTime(), userSetting.getId()) : null;
  }

  public UserDataVM convertUserData(User user, String token) {
    return new UserDataVM(user.getName(), user.getPermissions(), token);
  }

  public Page<UserVM> convertUsers(Page<User> users) {
    return users.map(user -> new UserVM(user.getName(), user.getRole(), user.getId()));
  }

  public List<UserDropdownVM> convertUsersForDropdown(List<User> allForDropdown) {
    if (allForDropdown == null) {
      return new ArrayList<>();
    }
    return allForDropdown.stream().map(user -> new UserDropdownVM(user.getName(), user.getId())).collect(Collectors.toList());
  }
}
