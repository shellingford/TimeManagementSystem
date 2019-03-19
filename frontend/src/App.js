import React from 'react';
import './App.css';
import LoginPage from "./components/login/login";
import RegisterPage from "./components/register/register";
import HomePage from "./components/homepage/homepage";
import PrivateRoute from "./components/privateRoute/privateRoute";
import { Switch } from "react-router";
import { Router, Route, Redirect } from "react-router-dom";
import axios from "axios";
import History from './components/common/History'

let history = History;
axios.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.authenticated = false;
      history.push('/login');
    }
    return Promise.reject(error);
  }
);

const App = () => (
  <Router history={history}>
    <div className="ui container">
      <Switch>
        <Redirect exact from='/' to="/homepage" />
        <Route location={window.location} path="/login" exact component={LoginPage} />
        <Route location={window.location} path="/register" exact component={RegisterPage} />
        <PrivateRoute location={window.location} path="/homepage" exact component={HomePage} />
      </Switch>
    </div>
  </Router>
);

export default App;
