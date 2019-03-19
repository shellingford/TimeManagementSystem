import React from 'react';
import LogoutButton from "../logout/logout"
import { Menu, Label, Segment, Dropdown } from 'semantic-ui-react'
import TimeEntries from '../timeentries/TimeEntries'
import UserSetting from '../usersetting/UserSetting'
import Users from '../users/Users'
import PermissionEnum from '../common/PermissionEnum'
import Api from './HomepageApi';
import './Homepage.css';

class HomePage extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            error: '',
            activeItem: 'timeEntries',
            userName: localStorage.userName,
            userPermissions: this.getUserPermissions(),
            users: [],
            userId: null
        };

        if (this.isAdmin()) {
            this.getUsers();
        }
    }

    getUsers = () => {
        return Api.getUsersForDropdown().then((response) => {
            let userId = response.data.find(user => user.text === this.state.userName).value;
            this.setState({ users: response.data, userId });
        });
    };

    getUserPermissions = () => {
        if (localStorage.userPermissions) {
            return JSON.parse(localStorage.userPermissions);
        }
        return [];
    }

    onSubmit = () => {
    };
    
    onChange = e => {
        const { name, value } = e.target;
        this.setState({ [name]: value });
    };

    handleItemClick = (e, { name }) => this.setState({ activeItem: name });

    canShowUsers = () => {
        return this.state.userPermissions.includes(PermissionEnum.manager);
    }

    isAdmin = () => {
        return this.state.userPermissions.includes(PermissionEnum.admin);
    }

    onUserChange = (e, {value}) => {
        this.setState({ userId: value });
    }

    getPage = (activeItem) => {
        const { users, userId } = this.state;
        let page;
        let userData;
        if (activeItem === 'timeEntries') {
            userData = <div>
                <UserSetting userId={userId}/>
                <TimeEntries userId={userId} />
            </div> 
        } 
        if (activeItem === 'users') {
            userData = <div>
                <Users/>
            </div> 
        } 
        if (this.isAdmin()) {
            page = <Segment padded>
                      <Label attached='top'>User data</Label>
                      <Dropdown placeholder='Select user' fluid selection options={users} defaultValue={userId} onChange={this.onUserChange} className="userDropdown"/>
                      {userData}
                  </Segment>
        } else {
            page = userData;
        }
        return page;
    }

    render() {
        const { activeItem } = this.state;
        let page = this.getPage(activeItem);
        return (
            <div>
                <Menu>
                    <Menu.Item name='timeEntries' active={activeItem === 'timeEntries'} onClick={this.handleItemClick}>
                        Time Entries
                    </Menu.Item>
                    {
                        this.canShowUsers() ? 
                            <Menu.Item name='users' active={activeItem === 'users'} onClick={this.handleItemClick}>
                                Users
                            </Menu.Item>
                            : <div></div>
                    }
                    <Menu.Menu position='right'>
                        <div className="username">
                            { this.state.userName }
                        </div>
                        <LogoutButton/>
                    </Menu.Menu>
                </Menu>
                {page}
            </div>
        )
    }
}

export default HomePage; 
