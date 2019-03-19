package tms.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tms.bl.UserSettingService;
import tms.bl.ViewModelService;
import tms.model.view.AddUserSettingVM;
import tms.model.view.EditUserSettingVM;
import tms.model.view.UserSettingVM;

@RestController
@RequestMapping("api/usersetting")
public class UserSettingController {

  @Autowired
  private UserSettingService userSettingService;

  @Autowired
  private ViewModelService viewModelService;

  @RequestMapping(value = "/", method = RequestMethod.POST)
  public void create(@RequestBody @Valid AddUserSettingVM addUserSettingVM) throws Exception {
    userSettingService.create(addUserSettingVM.getEndTime(), addUserSettingVM.getStartTime(), addUserSettingVM.getUserId());
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public UserSettingVM getForUser(@RequestParam(name = "userId", required = false) final Long userId) throws Exception {
    return viewModelService.convertUserSettings(userSettingService.getForUserWithAuth(userId));
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public void delete(@PathVariable("id") final Long id) throws Exception {
    userSettingService.delete(id);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public void update(@PathVariable("id") final Long id, @RequestBody @Valid EditUserSettingVM editUserSettingVM) throws Exception {
    userSettingService.update(id, editUserSettingVM.getStartTime(), editUserSettingVM.getEndTime());
  }

}
