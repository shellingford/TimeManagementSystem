import React from 'react';
import { Table, Pagination, Icon, Label, Button, Form, Segment, Dropdown, Modal } from "semantic-ui-react";
import Api from './UserApi';
import ValidationError from '../error/ValidationError'
import Translate from '../translate/translate'
import PageStateEnum from '../common/PageStateEnum'
import PermissionEnum from '../common/PermissionEnum'
import './Users.css'

class Users extends React.Component {

    defaultuser = () => {
        return {"name":"", "role": 1, "id": -1};
    }

    constructor(props) {
        super(props);

        if (localStorage.authenticated === "false") {
            this.props.history.push("/login");
        }
        if (!localStorage.userPermissions.includes(PermissionEnum.manager)) {
            this.props.history.push("/homepage");
        }

        this.state = {
            page: 1,
            totalPages: 1,
            users: [],
            pageState: PageStateEnum.view,
            userEdit: this.defaultuser(),
            errors: {},
            column: 'name',
            direction: 'ascending',
            passChangeOpen: false,
            deleteConfirmOpen: false,
        };
    }

    componentDidMount = () => {
        this.getUsers();
    };

    getUsers = () => {
        let params = {
            paged: true,
            page: this.state.page - 1,
            size: 10,
            sort: `${this.state.column},${this.state.direction === 'descending' ? 'desc' : 'asc'}`,
        };
        return Api.getAll(params).then((response) => {
            let users = response.data.content;
            let totalPages = response.data.totalPages;
            this.setState({ users, totalPages });
        });
    };

    handleErrors = err => {
        let errorFields = Translate.translateErrors(err.response.data);
        this.setState({ errors:errorFields });
    };

    onPageChange = (e, data) => {
        this.setState({ page: data.activePage }, this.getUsers);
    };
    
    onEditChange = (e, {name, value}) => {
        let editEntity = this.state.userEdit;
        editEntity[name] = value;
        let errors = this.state.errors;
        delete errors[name];
        this.setState({ userEdit: editEntity, errors });
    };
    
    onEdit = (user) => {
        this.setState({ pageState: PageStateEnum.edit, userEdit: {"name": user.name, "role": user.role, "id": user.id} });
    };

    onChangeSave = (userEdit) => {
        Api.update(userEdit.id, userEdit).then(() => this.setState({ pageState: PageStateEnum.view }, this.getUsers))
            .catch(this.handleErrors);
    };

    onCancelEdit = () => {
        if (this.state.pageState === PageStateEnum.edit) {
            this.setState({ pageState: PageStateEnum.view, userEdit: this.defaultuser() });
        }
        if (this.state.pageState === PageStateEnum.add) {
            let users = this.state.users;
            users.splice(-1,1); //remove fake last user
            this.setState({ pageState: PageStateEnum.view, users});
        }
    };

    showEdit(pageState, editId, id) {
        return (pageState === PageStateEnum.edit && editId === id) || (id === -1 && pageState === PageStateEnum.add);
    }

    changeToAddView() {
        let page = this.state.totalPages;
        this.setState({ pageState: PageStateEnum.add, page }, this.addEmptyUser);
    }

    addEmptyUser() {
        this.getUsers().then(() => {
            let userEdit = this.defaultuser();
            let users = this.state.users;
            users.push(userEdit);
            this.setState({ pageState: PageStateEnum.add, userEdit, users });
        });
    }

    getUserRoleName(role) {
        switch(role) {
            case 1: return "User";
            case 2: return "Manager";
            case 3: return "Admin";
            default: return "n/a";
        }
    }

    getRoles() {
        return [{"text": "User", "value": 1}, {"text": "Manager", "value": 2}, {"text": "Admin", "value": 3}];
    }

    onRoleChange = (e, {value}) => {
        let userEdit = this.state.userEdit;
        userEdit.role = value;
        this.setState({ userEdit });
    }

    showPassChange = (user) => {
        this.setState({ userEdit: {"name": user.name, "role": user.role, "id": user.id}, passChangeOpen: true, errors: {} });
    }

    closePassChange = () => {
        this.setState({ userEdit: this.defaultuser, passChangeOpen: false });
    }

    showDeleteConfirm = (user) => {
        this.setState({ userEdit: {"name": user.name, "role": user.role, "id": user.id}, deleteConfirmOpen: true });
    }

    closeDeleteConfirm = () => {
        this.setState({ userEdit: this.defaultuser, deleteConfirmOpen: false });
    }

    deleteUser = () => {
        Api.delete(this.state.userEdit.id).then(() => this.setState({ userEdit: this.defaultuser, deleteConfirmOpen: false }, this.getUsers));
    };

    onSortChange = clickedColumn => () => {
        if (this.state.pageState !== PageStateEnum.view) {
            return;
        }
        const { column, direction } = this.state;

        if (column !== clickedColumn) {
            this.setState({ column: clickedColumn, direction: 'ascending' }, this.getUsers);
            return;
        }

        this.setState({ direction: direction === 'ascending' ? 'descending' : 'ascending' }, this.getUsers);
    }

    changePass = () => {
        Api.update(this.state.userEdit.id, this.state.userEdit).then(() => {
            this.setState({ userEdit: this.defaultuser, passChangeOpen: false });
        }).catch(this.handleErrors);
    }

    render() {
        const { users, page, totalPages, pageState, userEdit, errors, column, direction, passChangeOpen, deleteConfirmOpen } = this.state;
        return (
            <Segment padded>
                <Label attached='top'>Users</Label>
                <Table striped compact unstackable sortable>
                    <Table.Header>
                        <Table.Row>
                            <Table.HeaderCell width="2" textAlign="center" sorted={column === 'name' ? direction : null} onClick={this.onSortChange('name')}>
                                Name
                            </Table.HeaderCell>
                            <Table.HeaderCell width="1" textAlign="center" sorted={column === 'role' ? direction : null} onClick={this.onSortChange('role')}>
                                Role
                            </Table.HeaderCell>
                            <Table.HeaderCell width="1" textAlign="center">
                                Password
                            </Table.HeaderCell>
                            <Table.HeaderCell width="2" textAlign="center">
                                Actions
                            </Table.HeaderCell>
                        </Table.Row>
                    </Table.Header>
                    <Table.Body>
                        {users.map(user => 
                            <Table.Row key={user.id}>
                                <Table.Cell textAlign="center" width="5">
                                    { this.showEdit(pageState, userEdit.id, user.id) ? 
                                        <Form.Field error={!!errors.name}>
                                            <Form.Input placeholder="Name..." name="name" value={userEdit.name} onChange={ this.onEditChange }
                                                        style={{width: '250px'}}/>
                                            {errors.name && <ValidationError text={errors.name} />}
                                        </Form.Field>
                                        : user.name
                                    }
                                </Table.Cell>
                                <Table.Cell textAlign="center" width="3">
                                    { this.showEdit(pageState, userEdit.id, user.id) ? 
                                        <Form.Field error={!!errors.role}>
                                            <Dropdown placeholder='Select role' fluid selection options={this.getRoles()} defaultValue={user.role} onChange={this.onRoleChange}/>
                                            {errors.role && <ValidationError text={errors.role} />}
                                        </Form.Field>
                                        : this.getUserRoleName(user.role)
                                    }
                                </Table.Cell>
                                <Table.Cell textAlign="center" width="1">
                                    <Button animated='vertical' size="tiny" onClick={() => this.showPassChange(user)}  disabled={pageState !== PageStateEnum.view}>
                                        <Button.Content hidden>Change</Button.Content>
                                        <Button.Content visible>
                                            <Icon name='key' />
                                        </Button.Content>
                                    </Button>
                                </Table.Cell>
                                <Table.Cell textAlign="center">
                                    { this.showEdit(pageState, userEdit.id, user.id) ?
                                        <div>
                                            <Button animated='vertical' size="tiny" onClick={() => this.onChangeSave(userEdit)}>
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
                                            <Button animated='vertical' size="tiny" onClick={() => this.onEdit(user)} disabled={pageState === PageStateEnum.add}>
                                                <Button.Content hidden>Edit</Button.Content>
                                                <Button.Content visible>
                                                    <Icon name='edit' />
                                                </Button.Content>
                                            </Button>
                                            <Button animated='vertical' size="tiny" onClick={() => this.showDeleteConfirm(user)} disabled={pageState === PageStateEnum.add}>
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
                <div>
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
                    <Button floated='right' icon labelPosition='left' size='medium' className="addNewButton" onClick={() => this.changeToAddView()} disabled={pageState !== PageStateEnum.view}>
                        <Icon name='file alternate outline' /> New
                    </Button>
                </div>
                <Modal size='mini' open={passChangeOpen} onClose={this.closePassChange}>
                    <Modal.Header>Change password</Modal.Header>
                    <Modal.Content>
                        <Form.Group>
                            <Form.Field error={!!errors.password}>
                                <Form.Input label="Password" placeholder="Password" name="password" value={userEdit.password} onChange={ this.onEditChange } className="pass"/>
                                {errors.password && <ValidationError text={errors.password} />}
                            </Form.Field>
                            <Form.Field error={!!errors.confirmPassword}>
                                <Form.Input type="password" label="Confirm password" placeholder="Password" name="confirmPassword" className="confirmPass"
                                            value={userEdit.confirmPassword} onChange={ this.onEditChange }/>
                                {errors.confirmPassword && <ValidationError text={errors.confirmPassword} />}
                            </Form.Field>
                        </Form.Group>
                    </Modal.Content>
                    <Modal.Actions>
                        <Button negative onClick={this.closePassChange}>Cancel</Button>
                        <Button positive icon='checkmark' labelPosition='right' content='Change' onClick={this.changePass} />
                    </Modal.Actions>
                </Modal>
                <Modal size='mini' open={deleteConfirmOpen} onClose={this.closeDeleteConfirm}>
                    <Modal.Header>Delete user</Modal.Header>
                    <Modal.Content>
                        <p>Are you sure you want to delete user?</p>
                    </Modal.Content>
                    <Modal.Actions>
                        <Button negative onClick={this.closeDeleteConfirm} content='Cancel'/>
                        <Button positive content='Delete' onClick={this.deleteUser} />
                    </Modal.Actions>
                </Modal>
            </Segment>
        )
    }
}

export default Users; 
