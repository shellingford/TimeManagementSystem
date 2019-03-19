package tms.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import tms.model.entity.TimeEntry;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long>, JpaSpecificationExecutor<TimeEntry> {

  public List<TimeEntry> findAllByUserId(long userId);

  @Query("SELECT te FROM TimeEntry te WHERE (?1 = te.userId OR ?1 IS NULL) AND (te.startTime BETWEEN IFNULL(?2, '2000-01-01') AND IFNULL(?3, '2100-01-01'))")
  public Page<TimeEntry> findAllBetween(Long userId, Instant from, Instant to, Pageable pageable);

  @Modifying
  @Transactional
  @Query(value = "INSERT INTO time_entry (duration, user_id, description, start_time) VALUES (?1, ?2, ?3, ?4)", nativeQuery = true)
  public void saveTimeEntry(Integer duration, Long userId, String description, Instant startTime);
}
