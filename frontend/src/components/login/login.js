import React from 'react';
import {
    Form,
    Grid,
  } from 'semantic-ui-react';
import Api from './LoginApi';
import { Link } from "react-router-dom";
import Translate from '../translate/translate'
import ValidationError from "../error/ValidationError";
import axios from "axios";

class LoginPage extends React.Component {
    constructor(props) {
        super(props);

        if (localStorage.authenticated === "true") {
            this.props.history.push("/homepage");
        }

        this.state = {
            name: '',
            password: '',
            submitted: false,
            loading: false,
            error: '',
            errors: {},
        };
    }

    validate = (name, password) => {
      const errors = {};
      if (!name) errors.name = Translate.translate("missing");
      if (!password) errors.password = Translate.translate("missing");
      return errors;
    };

    onSubmit = () => {
        localStorage.authenticated = true;
        const errors = this.validate(this.state.name, this.state.password);

        Api.login(this.state.name, this.state.password).then((responseData) => {
            localStorage.authenticated = true;
            let userData = responseData.data;
            axios.defaults.headers.common.authorization = `${userData.token}`;
            localStorage.userName = userData.name;
            localStorage.userPermissions = JSON.stringify(userData.permissions);
            this.props.history.push("/homepage");
        })
        .catch(err => {
            localStorage.authenticated = false;
            if (err.response.status === 401) {
                errors.password = Translate.translate("login_failed");
                this.setState({ errors });
            } else {
                let errorFields = Translate.translateErrors(err.response.data);
                this.setState({ errors:errorFields });
            }
        });
    };
    
    onChange = e => {
        const { name, value } = e.target;
        this.setState({ [name]: value });
    };

    render() {
        const { name, password, errors } = this.state;
        return (
            <Grid style={{ height: "100%", top: 20, bottom: 20, left: 20, right: 20, position: "absolute" }}
                verticalAlign="middle" centered>
                <Grid.Column style={{ maxWidth: 450 }}>
                <Form onSubmit={this.onSubmit}>
                    <Form.Group>
                        <Form.Field error={!!errors.name}>
                            <Form.Input label="Name" placeholder="Name" name="name" value={name} onChange={ this.onChange }/>
                            {errors.name && <ValidationError text={errors.name} />}
                        </Form.Field>
                        <Form.Field error={!!errors.password}>
                            <Form.Input label="Password" placeholder="Password" name="password" value={password} onChange={ this.onChange }/>
                            {errors.password && <ValidationError text={errors.password} />}
                        </Form.Field>
                    </Form.Group>
                    <Form.Button>Login</Form.Button>
                </Form>
                <div style={{ marginTop: 10 }}>
                    Don't have an account? <Link to="/register">Register</Link>
                </div>
                </Grid.Column>
            </Grid>
        );
    }
}

export default LoginPage; 
