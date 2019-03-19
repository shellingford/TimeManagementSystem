package tms.bl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import tms.bl.error.BusinessLogicException;
import tms.bl.error.ValidationException;
import tms.config.user.UserContext;
import tms.model.entity.TimeEntry;
import tms.model.entity.UserSetting;
import tms.repository.TimeEntryRepository;

@SuppressWarnings("unchecked")
public class TimeEntryServiceTest {

  @Mock
  private TimeEntryRepository timeEntryRepository;

  @Mock
  private UserSettingService userSettingService;

  @InjectMocks
  private TimeEntryService timeEntryService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    UserContext.clear();
  }

  private void setupAdmin(long userId) {
    List<String> permissions = new ArrayList<>();
    permissions.add("admin");
    UserContext.setData(userId, permissions);
  }

  private void setupUser(long userId) {
    List<String> permissions = new ArrayList<>();
    permissions.add("user");
    UserContext.setData(userId, permissions);
  }

  @Test
  public void getAllUser() {
    long userId = 1;
    setupUser(userId);
    Pageable pageable = Mockito.mock(Pageable.class);
    Page<TimeEntry> timeEntryPage = Mockito.mock(Page.class);

    Instant from = Instant.now();
    Instant to = Instant.now();
    Mockito.when(timeEntryRepository.findAllBetween(userId, from, to, pageable)).thenReturn(timeEntryPage);
    Page<TimeEntry> timeEntries = timeEntryService.getAll(from, to, pageable, null);

    assertEquals(timeEntryPage, timeEntries);

    boolean thrownException = false;
    try {
      timeEntryService.getAll(from, to, pageable, userId);
    } catch (BusinessLogicException e) {
      thrownException = true;
    }
    assertTrue(thrownException);

    from = Instant.now();
    to = Instant.now().minusSeconds(100);
    thrownException = false;
    try {
      timeEntryService.getAll(from, to, pageable, null);
    } catch (ValidationException e) {
      thrownException = true;
      assertEquals("from_after_to", e.getMessage());
    }
    assertTrue(thrownException);
  }

  @Test
  public void getAllAdmin() {
    long userId = 1;
    setupAdmin(3L);

    Pageable pageable = Mockito.mock(Pageable.class);
    Page<TimeEntry> timeEntryPage = Mockito.mock(Page.class);
    timeEntryPage.getContent().add(new TimeEntry(2L, "", null, 434, userId));
    Instant from = Instant.now();
    Instant to = Instant.now();
    Mockito.when(timeEntryRepository.findAllBetween(userId, from, to, pageable)).thenReturn(timeEntryPage);
    Page<TimeEntry> timeEntries = timeEntryService.getAll(from, to, pageable, userId);

    assertEquals(timeEntries.getContent(), timeEntryPage.getContent());
  }

  @Test
  public void getAllWithPreferredTimes() {
    long userId = 1;
    setupAdmin(3L);

    Pageable pageable = Mockito.mock(Pageable.class);
    List<TimeEntry> content = new ArrayList<>();
    content.add(new TimeEntry(2L, "", Instant.now(), 1, userId));
    content.add(new TimeEntry(3L, "", Instant.now(), 100, userId));
    Page<TimeEntry> timeEntryPage = new PageImpl<>(content);
    Instant from = Instant.now();
    Instant to = Instant.now();
    Mockito.when(timeEntryRepository.findAllBetween(userId, from, to, pageable)).thenReturn(timeEntryPage);

    LocalTime startTime = LocalTime.now().minusMinutes(5);
    LocalTime endTime = LocalTime.now().plusSeconds(100);
    UserSetting userSetting = new UserSetting(startTime, endTime, userId);
    Mockito.when(userSettingService.getForUser(userId)).thenReturn(userSetting);

    Page<TimeEntry> timeEntries = timeEntryService.getAll(from, to, pageable, userId);
    assertEquals(content.size(), timeEntries.getContent().size());

    assertTrue(timeEntries.getContent().get(0).isDuringPreferredTime());
    assertFalse(timeEntries.getContent().get(1).isDuringPreferredTime());
  }

  @Test
  public void create() {
    long userId = 1;
    setupUser(userId);

    String description = "123";
    Instant from = Instant.now();
    int duration = 12;
    timeEntryService.create(description, from, duration, null);
    Mockito.verify(timeEntryRepository, times(1)).saveTimeEntry(duration, userId, description, from);
  }

  @Test(expected = BusinessLogicException.class)
  public void createNotAdmin() {
    long userId = 1;
    setupUser(userId);
    String description = "123";
    Instant from = Instant.now();
    int duration = 12;
    timeEntryService.create(description, from, duration, 4L);
  }

  @Test
  public void createAdmin() {
    setupAdmin(4L);
    long userId = 1;
    String description = "123";
    Instant from = Instant.now();
    int duration = 12;
    timeEntryService.create(description, from, duration, userId);
    Mockito.verify(timeEntryRepository, times(1)).saveTimeEntry(duration, userId, description, from);
  }

  @Test
  public void update() {
    long userId = 1;
    setupUser(userId);
    String description = "123";
    Instant from = Instant.now();
    int duration = 12;
    long entryId = 5;

    TimeEntry timeEntry = new TimeEntry(entryId, description, from, duration, userId);
    Mockito.when(timeEntryRepository.findById(entryId)).thenReturn(Optional.of(timeEntry));

    String description2 = "123";
    Instant from2 = Instant.now();
    int duration2 = 25;
    timeEntryService.update(entryId, description2, from2, duration2);
    timeEntry.update(description2, from2, duration2);

    Mockito.verify(timeEntryRepository, times(1)).save(timeEntry);
  }

  @Test(expected = BusinessLogicException.class)
  public void updateNotAdmin() {
    long userId = 1;
    setupUser(userId);
    String description = "123";
    Instant from = Instant.now();
    int duration = 12;
    long entryId = 5;

    TimeEntry timeEntry = new TimeEntry(entryId, description, from, duration, 4L);
    Mockito.when(timeEntryRepository.findById(entryId)).thenReturn(Optional.of(timeEntry));

    timeEntryService.update(entryId, null, null, null);
  }

  @Test
  public void updateAdmin() {
    long userId = 5;
    setupAdmin(4L);
    String description = "123";
    Instant from = Instant.now();
    int duration = 12;
    long entryId = 5;

    TimeEntry timeEntry = new TimeEntry(entryId, description, from, duration, userId);
    Mockito.when(timeEntryRepository.findById(entryId)).thenReturn(Optional.of(timeEntry));

    String description2 = "123";
    Instant from2 = Instant.now();
    int duration2 = 25;
    timeEntryService.update(entryId, description2, from2, duration2);
    timeEntry.update(description2, from2, duration2);

    Mockito.verify(timeEntryRepository, times(1)).save(timeEntry);
  }

  @Test
  public void delete() {
    long userId = 1;
    setupUser(userId);
    String description = "123";
    Instant from = Instant.now();
    int duration = 12;
    long entryId = 5;

    TimeEntry timeEntry = new TimeEntry(entryId, description, from, duration, userId);
    Mockito.when(timeEntryRepository.findById(entryId)).thenReturn(Optional.of(timeEntry));

    timeEntryService.delete(entryId);

    Mockito.verify(timeEntryRepository, times(1)).delete(timeEntry);
  }

  @Test(expected = BusinessLogicException.class)
  public void deleteNotAdmin() {
    long userId = 1;
    setupUser(userId);
    String description = "123";
    Instant from = Instant.now();
    int duration = 12;
    long entryId = 5;

    TimeEntry timeEntry = new TimeEntry(entryId, description, from, duration, 4L);
    Mockito.when(timeEntryRepository.findById(entryId)).thenReturn(Optional.of(timeEntry));

    timeEntryService.delete(entryId);
  }

  @Test
  public void deleteAdmin() {
    long userId = 5;
    setupAdmin(4L);
    String description = "123";
    Instant from = Instant.now();
    int duration = 12;
    long entryId = 5;

    TimeEntry timeEntry = new TimeEntry(entryId, description, from, duration, userId);
    Mockito.when(timeEntryRepository.findById(entryId)).thenReturn(Optional.of(timeEntry));

    timeEntryService.delete(entryId);

    Mockito.verify(timeEntryRepository, times(1)).delete(timeEntry);
  }
}
