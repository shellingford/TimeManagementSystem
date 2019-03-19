package tms.bl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import tms.bl.error.ValidationException;

public class PasswordServiceTest {

  private PasswordService passService;

  @Before
  public void setUp() {
    passService = new PasswordService();
  }

  @Test(expected = ValidationException.class)
  public void newPassNull() {
    passService.passwordValidation("", null, "");
  }

  @Test(expected = ValidationException.class)
  public void newPassEmpty() {
    passService.passwordValidation("", "", "");
  }

  @Test(expected = ValidationException.class)
  public void shortNewPass() {
    passService.passwordValidation("test", "test", "username");
  }

  @Test(expected = ValidationException.class)
  public void newPassSameAsUsername() {
    passService.passwordValidation("test", "someLongUsername", "someLongUsername");
  }

  @Test(expected = ValidationException.class)
  public void newPassWithoutNumber() {
    passService.passwordValidation("test", "somePass!bla", "test");
  }

  @Test(expected = ValidationException.class)
  public void newPassWithoutSpecialChar() {
    passService.passwordValidation("test", "somePass8bla", "test");
  }

  @Test(expected = ValidationException.class)
  public void newPassWithoutLowerCaseChar() {
    passService.passwordValidation("test", "SOMEPASS8BLA!", "test");
  }

  @Test(expected = ValidationException.class)
  public void newPassWithoutUpperCaseChar() {
    passService.passwordValidation("test", "somepass8bla!", "test");
  }

  @Test
  public void newPassTooManyConsIdentChars() {
    boolean thrownException = false;
    try {
      passService.passwordValidation("test", "somePasss8bla!", "test");
    } catch (ValidationException e) {
      thrownException = true;
    }
    assertTrue(thrownException);

    thrownException = false;
    try {
      passService.passwordValidation("test", "somePaLLL8bla!", "test");
    } catch (ValidationException e) {
      thrownException = true;
    }
    assertTrue(thrownException);

    thrownException = false;
    try {
      passService.passwordValidation("test", "somePas888bla!", "test");
    } catch (ValidationException e) {
      thrownException = true;
    }
    assertTrue(thrownException);
  }

  @Test(expected = ValidationException.class)
  public void validPasswordConfirmDiff() {
    passService.passwordValidation("someOldPassword", "comPLex@3nougHPass!", "test");
  }

  @Test
  public void newOldPassTooSimilar() {
    boolean thrownException = false;
    try {
      // levenshtein distance (case insensitive) = 1
      passService.passwordValidation("somePasts8bla?", "somePasts8bla!", "test");
    } catch (ValidationException e) {
      thrownException = true;
    }
    assertTrue(thrownException);

    thrownException = false;
    try {
      // levenshtein distance (case insensitive) = 5
      passService.passwordValidation("5amEpas3s8b1a?", "somePasts8bla!", "test");
    } catch (ValidationException e) {
      thrownException = true;
    }
    assertTrue(thrownException);
  }

  @Test
  public void validPassword() {
    passService.passwordValidation("comPLex@3nougHPass!", "comPLex@3nougHPass!", "testUser");
  }

  @Test
  public void passwordsMatch() {
    String pass = "testing123!D_";
    PasswordEncoder passDecoder = passService.getPasswordEncoder();
    boolean match = passService.passwordsMatch(pass, passDecoder.encode(pass));
    assertTrue(match);

    match = passService.passwordsMatch(pass, pass);
    assertFalse(match);
  }

}
