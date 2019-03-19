package tms.model.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class User extends AbstractPersistable<Long> {

  private String name;
  private String password;
  private int role;

  @Transient
  private List<Permission> permissions;
  @Transient
  private static final int defaultRole = 1;

  protected User() {
  }

  public User(long id, String name, int role) {
    this.name = name;
    this.role = role;
    this.setId(id);
  }

  public User(String name, int role, String passwordHash) {
    this.name = name;
    this.role = role;
    this.password = passwordHash;
  }

  public User(String name, String passwordHash) {
    this.name = name;
    this.password = passwordHash;
    this.role = defaultRole;
  }

  public User(long id, String name, String password) {
    this.name = name;
    this.password = password;
    this.setId(id);
  }

  public String getName() {
    return name;
  }

  public String getPassword() {
    return password;
  }

  public int getRole() {
    return role;
  }

  public List<String> getPermissions() {
    if (permissions == null) {
      return new ArrayList<>();
    }
    return permissions.stream().map(p -> p.getName()).collect(Collectors.toList());
  }

  public void setPermissions(List<Permission> permissions) {
    this.permissions = Collections.unmodifiableList(permissions);
  }

  public void update(String name, int role) {
    this.name = name;
    this.role = role;
  }

  public void updateWithNewPass(String name, int role, String passwordHash) {
    this.name = name;
    this.role = role;
    this.password = passwordHash;
  }

}
