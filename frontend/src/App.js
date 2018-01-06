import React, {Component} from 'react';
import './App.css';
import axios from 'axios';
import HeaderMenu from "./components/HeaderMenu";
import HomeView from "./components/HomeView";
import {Segment} from "semantic-ui-react";
import RegisterUserView from "./components/RegisterUserView";
import FooterComponent from "./components/FooterComponent";
import LoginView from "./components/LoginView";
import RepoManagerView from "./components/RepoManagerView";

export default class App extends Component {

  constructor(props) {
    super(props);
    this.state = {
      activeView: "home",
      loggedIn: null
    }
  }

  componentWillMount() {
    axios.get("api/login").then(
      res => {
        if (res.status === 200) {
          this.setState({loggedIn: res.data.userId})
        } else {
          this.setState({loggedIn: null})
        }
      },
      err => {
        this.setState({loggedIn: null})
      }
    )
  }

  render() {
    let view;
    switch (this.state.activeView) {
      case "register":
        view = <RegisterUserView/>;
        break;
      case "login":
        view = <LoginView loginFunc={this._doLogin}/>;
        break;
      case "repoManager":
        view = <RepoManagerView/>;
        break;
      default:
        view = <HomeView/>;
    }
    return (
      <div className="App">
        <HeaderMenu userId={this.state.loggedIn} activeView={this.state.activeView}
                    changeView={v => this.setState({activeView: v})} logoutFunc={this._doLogout}/>
        <Segment className="container" raised padded="very">
          {view}
        </Segment>
        <FooterComponent/>
      </div>
    );
  }

  _doLogin = name => {
    this.setState({loggedIn: name});
  };

  _doLogout = () => {
    axios.post("api/logout");
    this.setState({loggedIn: null})
  };
}
