import React from 'react';
import {
    Form,
    Grid,
  } from 'semantic-ui-react';
import Api from './RegisterApi';
import ValidationError from '../error/ValidationError'
import Translate from '../translate/translate'

class RegisterPage extends React.Component {
    constructor(props) {
        super(props);

        if (localStorage.authenticated === "true") {
            this.props.history.push("/homepage");
        }

        this.state = {
            name: '',
            password: '',
            confirmPassword: '',
            submitted: false,
            loading: false,
            errors: {}
        };
    }

    validate = (name, password, confirmPassword) => {
      const errors = {};
      if (!name) errors.name = Translate.translate("missing");
      if (!password) errors.password = Translate.translate("missing");
      if (!confirmPassword) errors.confirmPassword = Translate.translate("missing");
      if (password !== confirmPassword) errors.confirmPassword = Translate.translate("dont_match");
      return errors;
    };

    onSubmit = () => {
      const errors = this.validate(this.state.name, this.state.password, this.state.confirmPassword);
      this.setState({ errors });
      if (Object.keys(errors).length === 0) {
        Api.register(this.state.name, this.state.password, this.state.confirmPassword).then(() => this.props.history.push("/homepage"))
            .catch(err => {
                let errorFields = Translate.translateErrors(err.response.data);
                this.setState({ errors:errorFields });
            });
      }
    };
    
    onChange = e => {
        const { name, value } = e.target;
        this.setState({ [name]: value });
    };

    render() {
        const { name, password, confirmPassword, errors } = this.state;
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
                            <Form.Field error={!!errors.confirmPassword}>
                                <Form.Input type="password" label="Confirm password" placeholder="Password" name="confirmPassword"
                                            value={confirmPassword} onChange={ this.onChange }/>
                                {errors.confirmPassword && <ValidationError text={errors.confirmPassword} />}
                            </Form.Field>
                        </Form.Group>
                        <Form.Button>Register</Form.Button>
                    </Form>
                </Grid.Column>
            </Grid>
        );
    }
}

export default RegisterPage; 
