import React, {Component} from "react";
import {Header, Input, Message} from "semantic-ui-react";
import axios from 'axios';


export default class RepoManagerView extends Component {
  static propTypes = {};

  constructor(props) {
    super(props);
    this.state = {
      repoName: "",
      success: false,
      errorMsg: null
    }
  }

  render() {
    let message;
    if (this.state.success) {
      message = <Message
        success
        header='Created'
        content="Repo successfully created"
      />
    } else if (this.state.errorMsg) {
      message = <Message
        error
        header='Failure'
        content={this.state.errorMsg}
      />
    }
    return (
      <div>
        <Header content="Manage Repositories"/>
        <Input placeholder="New Repo..." value={this.state.repoName} onChange={this._onChange}
               action={{labelPosition: 'right', icon: 'plus', content: 'Create', onClick: this._createRepo}}/>
        {message}
      </div>
    )
  }

  _createRepo = () => {
    axios.post("api/repo", {name: this.state.repoName}).then(
      res => {
        if (res.status === 200) {
          this.setState({success: true, errorMsg: null});
        } else {
          this.setState({success: false, errorMsg: "Unknown Failure"});
          console.log(res);
        }
      },
      error => {
        if (error.response.status === 401) {
          this.setState({success: false, errorMsg: "Please login!"});
        } else if (error.response.status === 400) {
          this.setState({success: false, errorMsg: error.response.data});
        } else {
          this.setState({success: false, errorMsg: "Unknown Failure"});
          console.log(error);
        }
      }
    )
  };

  _onChange = (e, {value}) => {
    this.setState({repoName: value})
  };

}