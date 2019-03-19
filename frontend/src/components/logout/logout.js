import React from 'react';
import Api from './LogoutApi';
import { withRouter } from "react-router-dom";
import { Menu } from 'semantic-ui-react'
import axios from "axios";

class LogoutButton extends React.Component {
    handleItemClick = () => {
        Api.logout().then(() => {
            localStorage.authenticated = false;
            delete axios.defaults.headers.common.authorization;
            this.props.history.push("/login");
        });
    };

    render() {
        return (
            <Menu.Item name='logout' onClick={this.handleItemClick}/>
        );
    }
}

export default withRouter(LogoutButton); 
