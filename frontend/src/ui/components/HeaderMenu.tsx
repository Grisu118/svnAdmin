import * as React from "react";
import {Menu, MenuItemProps} from "semantic-ui-react";
import {View} from "../views/View";

interface HeaderMenuProps {
  activeView: View;
  changeView: (name: View) => void;
  logoutFunc: (event: React.MouseEvent<HTMLAnchorElement>, data: MenuItemProps) => void;
  userId: string | null;
}

interface Name {
  name: string;
}

export default class HeaderMenu extends React.Component<HeaderMenuProps, any> {

  render() {
    const activeView = this.props.activeView;

    let loginLogout;
    let registerUser;
    let repoManager;
    if (this.props.userId) {
      loginLogout = <Menu.Item name="logout" position="right" onClick={this.props.logoutFunc}/>;
      repoManager =
        <Menu.Item name="REPOMANAGER" active={activeView === View.REPOMANAGER}
                   onClick={this._handleItemClick}>Repositories</Menu.Item>;
    } else {
      loginLogout =
        <Menu.Item name="LOGIN" position="right" active={activeView === View.LOGIN}
                   onClick={this._handleItemClick}>Login</Menu.Item>;
      registerUser = <Menu.Item name="REGISTER" active={activeView === View.REGISTER} onClick={this._handleItemClick}>Register
        User</Menu.Item>;
    }
    return (
      <header>
        <Menu pointing secondary>
          <Menu.Item header>SVN Admin</Menu.Item>
          <Menu.Item name="HOME" active={activeView === View.HOME} onClick={this._handleItemClick}>Home</Menu.Item>
          {repoManager}
          {registerUser}
          {loginLogout}
        </Menu>
      </header>
    );
  }

  _handleItemClick = (e: any, o: Name) => this.props.changeView(View[o.name]);
}