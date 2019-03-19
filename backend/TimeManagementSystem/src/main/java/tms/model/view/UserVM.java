package tms.model.view;

public class UserVM {

  private String name;
  private int role;
  private long id;

  public UserVM(String name, int role, long id) {
    this.name = name;
    this.role = role;
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public int getRole() {
    return role;
  }

  public long getId() {
    return id;
  }

}
