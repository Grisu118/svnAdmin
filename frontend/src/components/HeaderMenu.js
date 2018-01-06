import React, {Component} from "react";
import {Menu} from "semantic-ui-react";
import PropTypes from 'prop-types';

export default class HeaderMenu extends Component {
  static propTypes = {
    activeView: PropTypes.string.isRequired,
    changeView: PropTypes.func.isRequired,
    logoutFunc: PropTypes.func.isRequired,
    userId: PropTypes.string
  };

  constructor(props) {
    super(props)
  }

  render() {
    const {activeView} = this.props;

    let loginLogout;
    let registerUser;
    if (this.props.userId) {
      loginLogout = <Menu.Item name="logout" position="right" onClick={this.props.logoutFunc}/>;
    } else {
      loginLogout =
        <Menu.Item name="login" position="right" active={activeView === 'login'} onClick={this._handleItemClick}/>;
      registerUser = <Menu.Item name='register' active={activeView === 'register'} onClick={this._handleItemClick}>Register
        User</Menu.Item>
    }
    return (
      <header>
        <Menu pointing secondary>
          <Menu.Item header>SVN Admin</Menu.Item>
          <Menu.Item name='home' active={activeView === 'home'} onClick={this._handleItemClick}/>
          {registerUser}
          {loginLogout}
        </Menu>
      </header>
    )
  }

  _handleItemClick = (e, {name}) => this.props.changeView(name);
}