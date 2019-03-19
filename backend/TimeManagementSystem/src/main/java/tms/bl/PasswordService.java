package tms.bl;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import tms.bl.error.BusinessLogicException;
import tms.bl.error.ValidationException;

/**
 * Service for operations regarding user's password.
 *
 */
@Service
public class PasswordService {

  /**
   * Array of special characters used in password.
   *
   * Characters need to be in ASCII ascending order.
   */
  private final char[] specialChars = { '!', '$', '*', '-', '.', '=', '?', '@', '_' };

  /**
   * Array of lower case characters used in password.
   */
  private final char[] lowerChars = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
      'y', 'z' };

  /**
   * Array of upper case characters used in password.
   */
  private final char[] upperChars = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
      'Y', 'Z' };

  /**
   * Array of number characters used in password.
   */
  private final char[] numberChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

  /**
   * Characters used for creating random password.
   */
  private final char[] passChars;

  /**
   * Minimum length of the password.
   */
  private final static int minPassLength = 10;

  public PasswordService() {
    passChars = ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(specialChars, lowerChars), upperChars), numberChars);
  }

  /**
   * Password encoder.
   */
  private BCryptPasswordEncoder passEncoder = new BCryptPasswordEncoder(11);

  /**
   * Checks if password from user input corresponds to the one in db.
   *
   * @param inputPass raw user password
   * @param dbPass    user password hash from db
   * @return true if they match, false otherwise
   */
  public boolean passwordsMatch(String inputPass, String dbPass) {
    return passEncoder.matches(inputPass, dbPass);
  }

  /**
   * Creates password hash and returns it.
   *
   * @param password clear text password
   * @return password hash
   */
  public String getPasswordHash(String password) {
    return passEncoder.encode(password);
  }

  /**
   * Checks if password is valid and complex enough.
   *
   * @param password        user password
   * @param confirmPassword retyped user password
   * @param userName        user name
   * @throws BusinessLogicException with message indicating what is wrong with password
   */
  public void passwordValidation(String password, String confirmPassword, String userName) {
    if (password == null) {
      throw new ValidationException("missing", "password");
    }
    if (confirmPassword == null) {
      throw new ValidationException("missing", "confirmPassword");
    }

    if (!password.equals(confirmPassword)) {
      throw new ValidationException("dont_match", "confirmPassword");
    }

    if (!passwordIsComplextEnough(password, userName)) {
      throw new ValidationException("complex_error", "password");
    }

  }

  /**
   * Password complexity requirements:<br>
   * 1) at least {@value #minPassLength} characters long <br>
   * 2) cannot be the same as username <br>
   * 3) contain at least one special, one lower case, one upper case and one number character <br>
   * 4) has no more than 2 consecutive identical characters <br>
   *
   * @param password user password
   * @param userName user name
   * @return true if password is complex enough, false otherwise
   */
  private boolean passwordIsComplextEnough(String password, String userName) {
    if (Strings.isNullOrEmpty(password)) {
      return false;
    }

    if (password.length() < minPassLength) {
      return false;
    }

    if (password.equalsIgnoreCase(userName)) {
      return false;
    }

    if (!containsAllChars(password)) {
      return false;
    }

    if (checkConsIdentChars(password)) {
      return false;
    }

    return true;
  }

  /**
   * Validates password.
   *
   * Password must meet complexity requirements:<br>
   * 1) at least 10 characters long <br>
   * 2) cannot be the same as username <br>
   * 3) contain at least one special, one lower case, one upper case and one number character <br>
   * 4) has no more than 2 consecutive identical characters <br>
   * 5) cannot be too similar to old password (at least 5 on Levenshtein distance)
   *
   * @param oldPassword old user password
   * @param newPassword new user password
   * @param userName    username
   * @throws BusinessLogicException with message indicating what is wrong with password
   */
  public void passwordChangeValidation(String oldPassword, String newPassword, String userName) {
    if (!passwordIsComplextEnough(newPassword, userName)) {
      throw new ValidationException("complex_error", "password");
    }

    if (getLevenshteinDistance(oldPassword, newPassword) <= 5) {
      throw new ValidationException("too_similar", "password");
    }
  }

  /**
   * Measures the amount of difference between two strings (passwords) as minimum number of edits needed to transform one string into the other. Strings
   * are compared as case insensitive.
   *
   * @param oldPass old password
   * @param newPass new password
   * @return levenshtein distance
   */
  private int getLevenshteinDistance(String oldPass, String newPass) {
    int oldPassLength = oldPass.length();
    int newPassLength = newPass.length();

    int[] p = new int[oldPassLength + 1];
    int[] d = new int[oldPassLength + 1];
    int[] dTemp;

    oldPass = oldPass.toLowerCase();
    newPass = newPass.toLowerCase();

    char t;
    int cost;

    for (int i = 0; i <= oldPassLength; i++) {
      p[i] = i;
    }

    for (int j = 1; j <= newPassLength; j++) {
      t = newPass.charAt(j - 1);
      d[0] = j;

      for (int i = 1; i <= oldPassLength; i++) {
        cost = oldPass.charAt(i - 1) == t ? 0 : 1;
        // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
        d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
      }

      // copy current distance counts to 'previous row' distance counts
      dTemp = p;
      p = d;
      d = dTemp;
    }

    // our last action in the above loop was to switch d and p, so p now
    // actually has the most recent cost counts
    return p[oldPassLength];
  }

  /**
   * Checks if password contains at least one special, one lower case, one upper case and one number character.
   *
   * @param newPassword new password
   * @return true if it contains above mentioned chars, false otherwise
   */
  private boolean containsAllChars(String newPassword) {
    int specials = 0;
    int uppers = 0;
    int lowers = 0;
    int numbers = 0;

    for (int i = 0; i < newPassword.length(); i++) {
      if (specials > 0 && uppers > 0 && lowers > 0 && numbers > 0) {
        return true;
      }

      if (Arrays.binarySearch(specialChars, newPassword.charAt(i)) >= 0) {
        specials++;
      }
      if (Arrays.binarySearch(upperChars, newPassword.charAt(i)) >= 0) {
        uppers++;
      }
      if (Arrays.binarySearch(lowerChars, newPassword.charAt(i)) >= 0) {
        lowers++;
      }
      if (Arrays.binarySearch(numberChars, newPassword.charAt(i)) >= 0) {
        numbers++;
      }
    }

    return specials > 0 && uppers > 0 && lowers > 0 && numbers > 0;
  }

  /**
   * Checks if string contains at least 3 consecutive identical characters.
   *
   * @param newPassword new password
   * @return
   */
  private boolean checkConsIdentChars(String newPassword) {
    Pattern pattern = Pattern.compile("([a-z0-9])\\1\\1", Pattern.CASE_INSENSITIVE);
    return pattern.matcher(newPassword).find();
  }

  public PasswordEncoder getPasswordEncoder() {
    return this.passEncoder;
  }

  public String getDefaultPassword() {
    return this.passEncoder.encode("k1U&0_FtrMsqp");
  }

  /**
   * Creates a random password.
   *
   * Random password is 15 characters long, contains at least one special, one lower case, one upper case and one number character.
   *
   * @return a random password
   */
  public String generateRandomPassword() {
    return this.passEncoder.encode(RandomStringUtils.random(minPassLength, passChars));
  }

}
