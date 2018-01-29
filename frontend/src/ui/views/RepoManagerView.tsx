import * as React from "react";
import { Header, Input, InputOnChangeData, Message } from "semantic-ui-react";
import Axios from 'axios';

interface RepoState {
  repoName: string;
  success: boolean;
  errorMsg: string | null;
}

export default class RepoManagerView extends React.Component<any, RepoState> {

  constructor(props: any) {
    super(props);
    this.state = {
      repoName: "",
      success: false,
      errorMsg: null
    };
  }

  render() {
    let message: JSX.Element | null = null;
    if (this.state.success) {
      message = <Message
        success
        header='Created'
        content="Repo successfully created"
      />;
    } else if (this.state.errorMsg) {
      message = <Message
        error
        header='Failure'
        content={this.state.errorMsg}
      />;
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
    Axios.post("api/repo", {name: this.state.repoName}).then(
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
        } else if (error.response.data && typeof error.response.data === "string") {
          this.setState({success: false, errorMsg: error.response.data});
        } else {
          this.setState({success: false, errorMsg: "Unknown Failure"});
          console.log(error);
        }
      }
    )
  };

  _onChange = (e: React.SyntheticEvent<HTMLInputElement>, data: InputOnChangeData) => {
    this.setState({repoName: data.value});
  };

}