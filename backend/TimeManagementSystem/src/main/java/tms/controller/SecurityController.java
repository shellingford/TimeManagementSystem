package tms.controller;

import javax.servlet.ServletException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import tms.bl.SecurityService;
import tms.bl.UserService;
import tms.bl.ViewModelService;
import tms.model.view.LoginVM;
import tms.model.view.RegisterVM;
import tms.model.view.UserDataVM;

/**
 * Controller class for security.
 *
 */
@RestController
@RequestMapping("api/security")
public class SecurityController {

  @Autowired
  private SecurityService securityService;

  @Autowired
  private UserService userService;

  @Autowired
  private ViewModelService viewModelService;

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public void register(@RequestBody @Valid RegisterVM registerVM) {
    securityService.register(registerVM.getName(), registerVM.getPassword(), registerVM.getConfirmPassword());
  }

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public UserDataVM login(@RequestBody @Valid LoginVM loginVM) {
    String token = securityService.login(loginVM.getName(), loginVM.getPassword());
    return viewModelService.convertUserData(userService.getCurrentUserWithPermissions(), token);
  }

  @RequestMapping(value = "/logout", method = RequestMethod.POST)
  public void logout() throws ServletException {
    securityService.logout();
  }

}
