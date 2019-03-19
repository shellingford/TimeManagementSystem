package tms.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import tms.bl.TimeEntryService;
import tms.bl.ViewModelService;
import tms.model.view.AddTimeEntryVM;
import tms.model.view.EditTimeEntryVM;
import tms.model.view.FilterTimeEntryVM;
import tms.model.view.TimeEntryVM;

@RestController
@RequestMapping("api/timeentry")
public class TimeEntryController {

  @Autowired
  private TimeEntryService timeEntryService;

  @Autowired
  private ViewModelService viewModelService;

  @RequestMapping(value = "/", method = RequestMethod.POST)
  public void create(@RequestBody @Valid AddTimeEntryVM addTimeEntryVM) throws Exception {
    timeEntryService.create(addTimeEntryVM.getDescription(), addTimeEntryVM.getFrom(), addTimeEntryVM.getDuration(), addTimeEntryVM.getUserId());
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public Page<TimeEntryVM> getAll(FilterTimeEntryVM filterTimeEntryVM, Pageable pageable) throws Exception {
    return viewModelService
        .convertTimeEntries(timeEntryService.getAll(filterTimeEntryVM.getFrom(), filterTimeEntryVM.getTo(), pageable, filterTimeEntryVM.getUserId()));
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public void delete(@PathVariable("id") final Long id) throws Exception {
    timeEntryService.delete(id);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public void update(@PathVariable("id") final Long id, @RequestBody @Valid EditTimeEntryVM editTimeEntryVM) throws Exception {
    timeEntryService.update(id, editTimeEntryVM.getDescription(), editTimeEntryVM.getFrom(), editTimeEntryVM.getDuration());
  }
}
