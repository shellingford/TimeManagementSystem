import React from 'react';
import Api from './UserSettingApi';
import { Grid, Icon, Button, Form, Label, Segment, Modal } from "semantic-ui-react";
import { TimeInput } from 'semantic-ui-calendar-react';
import ValidationError from '../error/ValidationError'
import Translate from '../translate/translate'
import moment from 'moment'
import PropTypes from "prop-types";

class UserSetting extends React.Component {

    defaultSetting = () => {
        let now = moment().format("HH:mm");
        return {"startTime": now, "endTime": now, "id": -1};
    }

    constructor(props) {
        super(props);

        if (localStorage.authenticated === "false") {
            this.props.history.push("/login");
        }

        this.state = {
            userSetting: this.defaultSetting(),
            errors: {},
            userId: props.userId,
            deleteConfirmOpen: false
        };
    }

    componentWillReceiveProps = props => {
        this.setState({ userId: props.userId }, this.getUserSetting);
    };

    componentDidMount = () => {
        this.getUserSetting();
    };

    getUserSetting = () => {
        let params = {};
        if (this.state.userId) {
            params.userId = this.state.userId;
        }
        return Api.get(params).then((response) => {
            let userSetting = response.data;
            if (!userSetting) {
                userSetting = this.defaultSetting();
            } else {
                userSetting.startTime = moment(userSetting.startTime, "HH:mm:ss").format("HH:mm");
                userSetting.endTime = moment(userSetting.endTime, "HH:mm:ss").format("HH:mm");
            }
            this.setState({ userSetting });
        });
    }

    onEditChange = (e, {name, value}) => {
        let userSetting = this.state.userSetting;
        userSetting[name] = value;
        this.setState({ userSetting, errors: {} });
    };

    onChangeSave = (userSetting) => {
        if (this.state.userId) {
            userSetting.userId = this.state.userId;
        }
        Api.update(userSetting.id, userSetting).then(() => this.getUserSetting())
            .catch(this.handleErrors);
    }

    handleErrors = err => {
        let errorFields = Translate.translateErrors(err.response.data);
        this.setState({ errors:errorFields });
    };

    onDelete = () => {
        Api.delete(this.state.userSetting.id).then(() => this.getUserSetting());
    };

    showDeleteConfirm = () => {
        this.setState({ deleteConfirmOpen: true });
    }

    closeDeleteConfirm = () => {
        this.setState({ deleteConfirmOpen: false });
    }

    deleteUserSetting = () => {
        Api.delete(this.state.userSetting.id).then(() => this.setState({ deleteConfirmOpen: false, userSetting: this.defaultSetting() }));
    };

    render() {
        const { userSetting, errors, deleteConfirmOpen } = this.state;
        return (
            <Segment padded>
                <Label attached='top'>Preferred working time</Label>
                <Grid divided='vertically' stackable>
                    <Grid.Row>
                        <Form.Field error={!!errors.startTime}>
                            <Label>Start time:</Label>
                            <TimeInput name="startTime" value={userSetting.startTime} iconPosition="left" onChange={this.onEditChange} className="startTime" />
                            {errors.startTime && <ValidationError text={errors.startTime} />}
                        </Form.Field>
                        <div className="column wide"></div>
                        <Form.Field error={!!errors.endTime}>
                            <Label>End time:</Label>
                            <TimeInput name="endTime" value={userSetting.endTime} iconPosition="left" onChange={this.onEditChange} className="endTime"/>
                            {errors.endTime && <ValidationError text={errors.endTime} />}
                        </Form.Field>
                        <div className="column two wide"></div>
                        <Button compact icon labelPosition='left' size='medium' onClick={() => this.onChangeSave(userSetting)} className="userSettingButton">
                            <Icon name='save outline' /> Save
                        </Button>
                        <Button compact icon labelPosition='left' size='medium' onClick={() => this.showDeleteConfirm()} disabled={userSetting.id <= 0} className="userSettingButton">
                            <Icon name='trash' /> Delete
                        </Button>
                    </Grid.Row>
                </Grid>
                <Modal size='mini' open={deleteConfirmOpen} onClose={this.closeDeleteConfirm}>
                    <Modal.Header>Delete user setting</Modal.Header>
                    <Modal.Content>
                        <p>Are you sure you want to delete user setting?</p>
                    </Modal.Content>
                    <Modal.Actions>
                        <Button negative onClick={this.closeDeleteConfirm} content='Cancel'/>
                        <Button positive content='Delete' onClick={this.deleteUserSetting} />
                    </Modal.Actions>
                </Modal>
            </Segment>
        );
    }
}

UserSetting.propTypes = {
  userId: PropTypes.number,
};

export default UserSetting; 
