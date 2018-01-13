import * as React from 'react';
import Axios from 'axios';
import {Container} from 'semantic-ui-react'

interface FooterState {
  version: string;
}

export default class FooterComponent extends React.Component<any, FooterState> {
  private year: number;

  constructor(props: any) {
    super(props);
    this.state = {
      version: ""
    };
    this.year = new Date().getFullYear();
  }

  componentWillMount() {
    Axios.get("version.json").then(res => {
      this.setState({version: res.data.version});
    });
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
    );
  }

}