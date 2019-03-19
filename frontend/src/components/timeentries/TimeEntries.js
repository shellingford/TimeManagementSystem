import React from 'react';
import { Table, Pagination, Icon, Label, Button, Form, Segment, Grid, Modal } from "semantic-ui-react";
import moment from 'moment'
import Api from './TimeEntriesApi';
import './TimeEntries.css';
import { DateInput, TimeInput } from 'semantic-ui-calendar-react';
import ValidationError from '../error/ValidationError'
import Translate from '../translate/translate'
import PageStateEnum from '../common/PageStateEnum'
import PropTypes from "prop-types";

class TimeEntries extends React.Component {

    constructor(props) {
        super(props);

        if (localStorage.authenticated === "false") {
            this.props.history.push("/login");
        }

        this.state = {
            page: 1,
            totalPages: 1,
            timeEntries: [],
            from: moment().startOf('day').format('DD-MM-YYYY'),
            to: moment().add(7, 'days').startOf('day').format('DD-MM-YYYY'),
            pageState: PageStateEnum.view,
            timeEntryEdit: {"date":null, "time": null, "duration": 1, "description": ""},
            errors: {},
            userId: props.userId,
            deleteConfirmOpen: false
        };
    }

    componentWillReceiveProps = props => {
        this.setState({ userId: props.userId }, this.getTimeEntries);
    };

    componentDidMount = () => {
        this.getTimeEntries();
    };

    getTimeEntries = () => {
        let params = {
            from: this.state.from ? moment(this.state.from, "DD-MM-YYYY").unix() : undefined,
            to: this.state.to ? moment(this.state.to, "DD-MM-YYYY").unix() : undefined,
            paged: true,
            page: this.state.page - 1,
            size: 10,
            sort: 'startTime'
        };
        if (this.state.userId) {
            params.userId = this.state.userId;
        }
        return Api.getAll(params).then((response) => {
            let timeEntries = response.data.content.map(timeEntry => {
                let dateTime = moment(timeEntry.from);
                return {
                    date: dateTime.format("MMM Do YYYY"),
                    time: dateTime.format("HH:mm"),
                    duration: timeEntry.duration,
                    description: timeEntry.description,
                    id: timeEntry.id,
                    duringPreferredTime: timeEntry.duringPreferredTime
                };
            });
            let totalPages = response.data.totalPages;
            this.setState({ timeEntries, totalPages });
        });
    };

    handleErrors = err => {
        let errorFields = Translate.translateErrors(err.response.data);
        if (errorFields.from) {
            if (!this.state.timeEntryEdit.date) {
                errorFields.date = errorFields.from; 
            }
            if (!this.state.timeEntryEdit.time) {
                errorFields.time = errorFields.from; 
            }
        }
        this.setState({ errors:errorFields });
    };

    onPageChange = (e, data) => {
        this.setState({ page: data.activePage }, this.getTimeEntries);
    };

    onDateChange = (e, {name, value}) => {
        if (this.state.hasOwnProperty(name)) {
            this.setState({ [name]: value }, this.getTimeEntries);
        }
    }
    
    onEditChange = (e, {name, value}) => {
        let editEntity = this.state.timeEntryEdit;
        editEntity[name] = value;
        let errors = this.state.errors;
        delete errors[name];
        this.setState({ timeEntryEdit: editEntity, errors });
    };
    
    onEdit = (timeEntry) => {
        this.setState({ pageState: PageStateEnum.edit, timeEntryEdit: timeEntry });
    };

    onChangeSave = (timeEntryEdit) => {
        timeEntryEdit.from = moment(timeEntryEdit.date + " " + timeEntryEdit.time, "MMM Do YYYY HH:mm").unix();
        if (this.state.userId) {
            timeEntryEdit.userId = this.state.userId;
        }
        Api.update(timeEntryEdit.id, timeEntryEdit).then(() => this.setState({ pageState: PageStateEnum.view }, this.getTimeEntries))
            .catch(this.handleErrors);
    };

    onCancelEdit = () => {
        if (this.state.pageState === PageStateEnum.edit) {
            this.setState({ pageState: PageStateEnum.view, timeEntryEdit: {"date":null, "time": null, "duration": 1, "description": ""} });
        }
        if (this.state.pageState === PageStateEnum.add) {
            let timeEntries = this.state.timeEntries;
            timeEntries.splice(-1,1); //remove fake last entry
            this.setState({ pageState: PageStateEnum.view, timeEntries});
        }
    };

    onDelete = (id) => {
        Api.delete(id).then(() => this.getTimeEntries());
    };

    showEdit(pageState, editId, id) {
        return (pageState === PageStateEnum.edit && editId === id) || (id === -1 && pageState === PageStateEnum.add);
    }

    changeToAddView() {
        let page = this.state.totalPages;
        this.setState({ pageState: PageStateEnum.add, page }, this.addEmptyTimeEntry);
    }

    addEmptyTimeEntry() {
        this.getTimeEntries().then(() => {
            let today = moment();
            let timeEntryEdit = {"id": -1, "date": today.format("MMM Do YYYY"), "time": today.format("HH:mm"), "duration": 1, "description": ""};
            let timeEntries = this.state.timeEntries;
            timeEntries.push(timeEntryEdit);
            this.setState({ pageState: PageStateEnum.add, timeEntryEdit, timeEntries });
        });
    }

    showDeleteConfirm = (timeEntry) => {
        this.setState({ timeEntryEdit: {"id": timeEntry.id}, deleteConfirmOpen: true });
    }

    closeDeleteConfirm = () => {
        let today = moment();
        this.setState({ timeEntryEdit: {"id": -1, "date": today.format("MMM Do YYYY"), "time": today.format("HH:mm"), "duration": 1, "description": ""}, deleteConfirmOpen: false });
    }

    deleteTimeEntry = () => {
        let today = moment();
        Api.delete(this.state.timeEntryEdit.id).then(() => this.setState({ 
            timeEntryEdit: {"id": -1, "date": today.format("MMM Do YYYY"), "time": today.format("HH:mm"), "duration": 1, "description": ""}, deleteConfirmOpen: false },
            this.getTimeEntries));
    };

    render() {
        const { timeEntries, page, totalPages, from, to, pageState, timeEntryEdit, errors, deleteConfirmOpen } = this.state;
        return (
            <Segment padded>
                <Label attached='top'>Time entries</Label>
                <Table striped selectable compact unstackable>
                    <Table.Header>
                        <Table.Row>
                            <Table.HeaderCell width="2" textAlign="center">
                                Date
                            </Table.HeaderCell>
                            <Table.HeaderCell width="1" textAlign="center">
                                Time
                            </Table.HeaderCell>
                            <Table.HeaderCell width="1" textAlign="center">
                                Duration(min)
                            </Table.HeaderCell>
                            <Table.HeaderCell width="9" textAlign="center">
                                Description
                            </Table.HeaderCell>
                            <Table.HeaderCell width="3" textAlign="center">
                                Actions
                            </Table.HeaderCell>
                        </Table.Row>
                    </Table.Header>
                    <Table.Body>
                        {timeEntries.map(timeEntry => 
                            <Table.Row key={timeEntry.id}>
                                <Table.Cell textAlign="center"
                                            className={timeEntry.duringPreferredTime === true ? 'duringPreferredTime' : (timeEntry.duringPreferredTime === false ? 'notDuringPreferredTime' : '')}>
                                    { this.showEdit(pageState, timeEntryEdit.id, timeEntry.id) ? 
                                        <Form.Field error={!!errors.date}>
                                            <DateInput name="date" value={timeEntryEdit.date} iconPosition="left" onChange={this.onEditChange}
                                                            style={{width: '143px'}} dateFormat="MMM Do YYYY"/>
                                            {errors.date && <ValidationError text={errors.date} />}
                                        </Form.Field>
                                        : timeEntry.date
                                    }
                                </Table.Cell>
                                <Table.Cell textAlign="center"
                                            className={timeEntry.duringPreferredTime === true ? 'duringPreferredTime' : (timeEntry.duringPreferredTime === false ? 'notDuringPreferredTime' : '')}>
                                    { this.showEdit(pageState, timeEntryEdit.id, timeEntry.id) ? 
                                        <Form.Field error={!!errors.time}>
                                            <TimeInput name="time" value={timeEntryEdit.time} iconPosition="left" onChange={this.onEditChange} style={{width: '90px'}}/>
                                            {errors.time && <ValidationError text={errors.time} />}
                                        </Form.Field>
                                        : timeEntry.time
                                    }
                                </Table.Cell>
                                <Table.Cell textAlign="center"
                                            className={timeEntry.duringPreferredTime === true ? 'duringPreferredTime' : (timeEntry.duringPreferredTime === false ? 'notDuringPreferredTime' : '')}>
                                    { this.showEdit(pageState, timeEntryEdit.id, timeEntry.id) ? 
                                        <Form.Field error={!!errors.duration}>
                                            <Form.Input placeholder="30" name="duration" value={timeEntryEdit.duration} onChange={ this.onEditChange }
                                                        type="number" style={{width: '75px'}} min="1"/>
                                            {errors.duration && <ValidationError text={errors.duration} />}
                                        </Form.Field>
                                        : timeEntry.duration
                                    }
                                </Table.Cell>
                                <Table.Cell textAlign="left"
                                            className={timeEntry.duringPreferredTime === true ? 'duringPreferredTime' : (timeEntry.duringPreferredTime === false ? 'notDuringPreferredTime' : '')}>
                                    { this.showEdit(pageState, timeEntryEdit.id, timeEntry.id) ? 
                                        <Form.Field error={!!errors.description}>
                                            <Form.Input placeholder="Description" name="description" value={timeEntryEdit.description} onChange={ this.onEditChange } style={{width:'80%'}}/>
                                            {errors.description && <ValidationError text={errors.description} />}
                                        </Form.Field>
                                        : timeEntry.description
                                    }
                                </Table.Cell>
                                <Table.Cell textAlign="center"
                                            className={timeEntry.duringPreferredTime === true ? 'duringPreferredTime' : (timeEntry.duringPreferredTime === false ? 'notDuringPreferredTime' : '')}>
                                    { this.showEdit(pageState, timeEntryEdit.id, timeEntry.id) ?
                                        <div>
                                            <Button animated='vertical' size="tiny" onClick={() => this.onChangeSave(timeEntryEdit)}>
                                                <Button.Content hidden>Save</Button.Content>
                                                <Button.Content visible>
                                                    <Icon name='save outline' />
                                                </Button.Content>
                                            </Button>
                                            <Button animated='vertical' size="tiny" onClick={() => this.onCancelEdit()}>
                                                <Button.Content hidden>Cancel</Button.Content>
                                                <Button.Content visible>
                                                    <Icon name='cancel' />
                                                </Button.Content>
                                            </Button>
                                        </div>
                                        :
                                        <div>
                                            <Button animated='vertical' size="tiny" onClick={() => this.onEdit(timeEntry)} disabled={pageState === PageStateEnum.add}>
                                                <Button.Content hidden>Edit</Button.Content>
                                                <Button.Content visible>
                                                    <Icon name='edit' />
                                                </Button.Content>
                                            </Button>
                                            <Button animated='vertical' size="tiny" onClick={() => this.showDeleteConfirm(timeEntry)} disabled={pageState === PageStateEnum.add}>
                                                <Button.Content hidden>Delete</Button.Content>
                                                <Button.Content visible>
                                                    <Icon name='trash' />
                                                </Button.Content>
                                            </Button>
                                        </div>
                                    }
                                </Table.Cell>
                            </Table.Row>
                        )}
                    </Table.Body>
                </Table>
                <Pagination
                    activePage={page}
                    ellipsisItem={{ content: <Icon name="ellipsis horizontal" />, icon: true }}
                    firstItem={{ content: <Icon name="angle double left" />, icon: true }}
                    lastItem={{ content: <Icon name="angle double right" />, icon: true }}
                    prevItem={{ content: <Icon name="angle left" />, icon: true }}
                    nextItem={{ content: <Icon name="angle right" />, icon: true }}
                    totalPages={totalPages} disabled={pageState === PageStateEnum.add}
                    onPageChange={this.onPageChange}
                />
                <Button floated='right' icon labelPosition='left' size='medium' className="addNewButton" onClick={() => this.changeToAddView()}>
                    <Icon name='file alternate outline' /> New
                </Button>
                <Grid divided='vertically' stackable >
                    <Grid.Row></Grid.Row>
                    <Grid.Row>
                        <Form.Field>
                                <Label>From</Label>
                                <DateInput
                                    name="from"
                                    placeholder="From"
                                    value={from}
                                    iconPosition="left"
                                    onChange={this.onDateChange}
                                />
                        </Form.Field>
                        <div className="column wide"></div>
                        <Form.Field>
                                <Label>To</Label>
                                <DateInput
                                    name="to"
                                    placeholder="To"
                                    value={to}
                                    iconPosition="left"
                                    onChange={this.onDateChange}
                                />
                        </Form.Field>
                    </Grid.Row>
                </Grid>
                <Modal size='mini' open={deleteConfirmOpen} onClose={this.closeDeleteConfirm}>
                    <Modal.Header>Delete time entry</Modal.Header>
                    <Modal.Content>
                        <p>Are you sure you want to delete time entry?</p>
                    </Modal.Content>
                    <Modal.Actions>
                        <Button negative onClick={this.closeDeleteConfirm} content='Cancel'/>
                        <Button positive content='Delete' onClick={this.deleteTimeEntry} />
                    </Modal.Actions>
                </Modal>
            </Segment>
        )
    }
}

TimeEntries.propTypes = {
  userId: PropTypes.number,
};

export default TimeEntries; 
