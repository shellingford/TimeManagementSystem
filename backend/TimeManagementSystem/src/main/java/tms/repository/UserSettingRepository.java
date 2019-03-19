package tms.repository;

import java.time.LocalTime;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tms.model.entity.UserSetting;

@Repository
public interface UserSettingRepository extends JpaRepository<UserSetting, Long>, JpaSpecificationExecutor<UserSetting> {

  public UserSetting findByUserId(long userId);

  @Modifying
  @Transactional
  @Query(value = "INSERT INTO user_setting (user_id, start_time, end_time) VALUES (?1, ?2, ?3)", nativeQuery = true)
  public void saveSetting(long userId, LocalTime startTime, LocalTime endTime);
}
