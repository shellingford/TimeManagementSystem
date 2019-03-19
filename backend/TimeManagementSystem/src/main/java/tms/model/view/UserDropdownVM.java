package tms.model.view;

public class UserDropdownVM {

  private String text; // user name
  private long value; // user id

  public UserDropdownVM(String text, long value) {
    this.text = text;
    this.value = value;
  }

  public String getText() {
    return text;
  }

  public long getValue() {
    return value;
  }

}
