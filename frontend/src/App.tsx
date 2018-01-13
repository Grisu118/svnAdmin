import * as React from 'react';
import Axios from 'axios';
import './App.css';
import HeaderMenu from "./ui/components/HeaderMenu";
import {Segment} from "semantic-ui-react";
import {View} from "./ui/views/View";
import FooterComponent from "./ui/components/FooterComponent";
import RegisterUserView from "./ui/views/RegisterUserView";
import LoginView from "./ui/views/LoginView";
import RepoManagerView from "./ui/views/RepoManagerView";
import HomeView from "./ui/views/HomeView";

interface AppState {
  activeView: View;
  loggedIn: string | null;
}

export default class App extends React.Component<any, AppState> {
  constructor(props: any) {
    super(props);
    this.state = {activeView: View.HOME, loggedIn: null};
  }

  componentWillMount() {
    Axios.get("api/login").then(
      res => {
        if (res.status === 200) {
          this.setState({loggedIn: res.data.userId});
        } else {
          this.setState({loggedIn: null});
        }
      },
      () => {
        this.setState({loggedIn: null});
      }
    );
  }

  render() {
    let view: JSX.Element;
    switch (this.state.activeView) {
      case View.REGISTER:
        view = <RegisterUserView/>;
        break;
      case View.LOGIN:
        view = <LoginView loginFunc={this._doLogin}/>;
        break;
      case View.REPOMANAGER:
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

  _doLogin = (name: string) => {
    this.setState({loggedIn: name});
  };

  _doLogout = () => {
    Axios.post("api/logout");
    this.setState({loggedIn: null, activeView: View.HOME})
  };
}