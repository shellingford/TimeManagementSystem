package tms.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tms.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

  /**
   * Returns user.
   *
   * @param name user name
   * @return user
   */
  public User findByName(@Param("name") String name);

  /**
   * Counts how many users are in db with the same name.
   *
   * @param name user name
   * @return count
   */
  public Long countByName(String name);

  /**
   * Saves new user.
   *
   * @param name         user name
   * @param role         user role
   * @param passwordHash user pass hash
   */
  @Transactional
  @Modifying
  @Query(value = "INSERT INTO user (name, role, password) VALUES (?1, ?2, ?3)", nativeQuery = true)
  public void save(String name, int role, String passwordHash);
}
