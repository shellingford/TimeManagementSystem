package tms.model.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserSession {

  @Id
  private String guid;
  private Long userId;

  protected UserSession() {
  }

  public UserSession(String guid, Long userId) {
    this.guid = guid;
    this.userId = userId;
  }

  public String getGuid() {
    return guid;
  }

  public Long getUserId() {
    return userId;
  }
}
