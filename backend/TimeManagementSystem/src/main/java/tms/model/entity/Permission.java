package tms.model.entity;

import javax.persistence.Entity;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Permission extends AbstractPersistable<Long> {

  private String name;
  private int role;

  protected Permission() {
  }

  public Permission(String name) {
    this.name = name;
  }

  public Permission(String name, int role) {
    this.name = name;
    this.role = role;
  }

  public String getName() {
    return name;
  }

  public int getRole() {
    return role;
  }
}
