import React, {Component} from "react";
import {Button, Form, Header, Message} from "semantic-ui-react";
import PropTypes from 'prop-types';
import axios from 'axios';

export default class LoginView extends Component {
  static propTypes = {
    loginFunc: PropTypes.func.isRequired
  };

  constructor(props) {
    super(props);
    this.state = {
      user: {
        username: "",
        pass: "",
      },
      error: false,
      success: false,
      failureMsg: ""
    }
  }

  render() {
    const {username, pass} = this.state.user;

    return (
      <div>
        <Header as="h2" content="Login"/>
        <Form error={this.state.error} success={this.state.success} onSubmit={this._handleSubmit}>
          <Form.Input label="Username" placeholder='jdoe' name='username' value={username}
                      onChange={this._handleChange}/>
          <Form.Input label="Password" type="password" name='pass' value={pass}
                      onChange={this._handleChange}/>
          <Message
            success
            header='Logged In'
            content="You are successfully logged in"
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
    axios.post("api/login", this.state.user).then(
      res => {
        console.log(res);
        this.setState({success: true, error: false});
        this.props.loginFunc(res.data.userId)
      },
      err => {
        if (err.response) {
          if (err.response.status === 400) {
            this.setState({failureMsg: err.response.data, error: true, success: false})
          } else if (err.response.status === 401) {
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