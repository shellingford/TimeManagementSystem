package tms.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import tms.bl.UserService;
import tms.bl.ViewModelService;
import tms.model.view.AddUserVM;
import tms.model.view.EditUserVM;
import tms.model.view.UserDropdownVM;
import tms.model.view.UserVM;

@RestController
@RequestMapping("api/user")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private ViewModelService viewModelService;

  @RequestMapping(value = "/", method = RequestMethod.POST)
  public void create(@RequestBody @Valid AddUserVM addUserVM) throws Exception {
    userService.create(addUserVM.getName(), addUserVM.getRole());
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public void delete(@PathVariable("id") final Long id) throws Exception {
    userService.delete(id);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public void update(@PathVariable("id") final Long id, @RequestBody @Valid EditUserVM editUserVM) throws Exception {
    userService.update(id, editUserVM.getName(), editUserVM.getRole(), editUserVM.getPassword(), editUserVM.getConfirmPassword());
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public Page<UserVM> getAll(Pageable pageable) throws Exception {
    return viewModelService.convertUsers(userService.getAll(pageable));
  }

  @RequestMapping(value = "/dropdown", method = RequestMethod.GET)
  public List<UserDropdownVM> getAllForDropdown() {
    return viewModelService.convertUsersForDropdown(userService.getAllForDropdown());
  }

}
