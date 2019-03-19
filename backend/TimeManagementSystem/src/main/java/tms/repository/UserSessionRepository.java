package tms.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import tms.model.entity.UserSession;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long>, JpaSpecificationExecutor<UserSession> {

  UserSession findByGuid(String guid);

  @Transactional
  void deleteByUserId(long userId);
}
