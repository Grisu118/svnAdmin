import React, {Component} from "react";
import {Container} from 'semantic-ui-react'
import axios from 'axios';

export default class FooterComponent extends Component {
  static propTypes = {};

  constructor(props) {
    super(props);
    this.state = {
      version: ""
    };
    this.year = new Date().getFullYear();
  }

  componentWillMount() {
    axios.get("version.json").then(res => {
      this.setState({version: res.data.version});
    })
  }

  render() {
    return (
      <Container as="footer" className="footer">
        <p>
          2017 - {this.year} © Grisu118 <br/>
          <a href="https://grisu118.ch" className="footer-link">Developed by https://grisu118.ch</a><br/>
          Version {this.state.version}
        </p>
      </Container>
    )
  }

}