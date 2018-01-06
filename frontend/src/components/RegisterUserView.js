import React, {Component} from "react";
import {Button, Form, Header, Message} from "semantic-ui-react";
import axios from 'axios';

export default class RegisterUserView extends Component {
  static propTypes = {};

  constructor(props) {
    super(props);
    this.state = {
      user: {
        token: "",
        username: "",
        pass: "",
        pass2: ""
      },
      error: false,
      success: false,
      failureMsg: ""
    }
  }

  render() {
    const {token, username, pass, pass2} = this.state.user;

    return (
      <div>
        <Header as="h2" content="Register User"/>
        <Form error={this.state.error} success={this.state.success} onSubmit={this._handleSubmit}>
          <Form.Input label="Token" placeholder='abcd-defd-2gfe-kg67' name='token' value={token}
                      onChange={this._handleChange}/>
          <Form.Input label="Username" placeholder='jdoe' name='username' value={username}
                      onChange={this._handleChange}/>
          <Form.Input label="Password" type="password" name='pass' value={pass}
                      onChange={this._handleChange}/>
          <Form.Input label="Password approval" type="password" name='pass2' value={pass2}
                      onChange={this._handleChange}/>
          <Message
            success
            header='Registered'
            content="You are successfully registered"
          />
          <Message
            error
            header='Failure'
            content={this.state.failureMsg}
          />
          <Button type='submit'>Submit</Button>
        </Form>
      </div>
    )
  }

  _handleSubmit = () => {
    axios.post("api/user", this.state.user).then(
      res => {
        console.log(res);
        this.setState({success: true, error: false})
      },
      err => {
        if (err.response) {
          if (err.response.status === 400) {
            this.setState({failureMsg: err.response.data, error: true, success: false})
          } else if (err.response.status === 403) {
            this.setState({failureMsg: err.response.data, error: true, success: false})
          } else {
            this.setState({failureMsg: "Unknown failure", error: true, success: false});
            console.log(err)
          }
        } else {
          this.setState({failureMsg: "Unknown failure", error: true, success: false});
          console.log(err)
        }
      }
    )
  };

  _handleChange = (e, {name, value}) => {
    let user = this.state.user;
    user[name] = value;
    this.setState({user: user});
  }

}