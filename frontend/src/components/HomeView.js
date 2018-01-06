import React, {Component} from "react";
import {Header} from "semantic-ui-react";

export default class HomeView extends Component {
  static propTypes = {};

  constructor(props) {
    super(props)
  }

  render() {

    return (
      <div>
        <Header content="SVN Admin"/>
        <p>
          Here you can register as svn user and create new repositories.
        </p>
      </div>
    )
  }

}