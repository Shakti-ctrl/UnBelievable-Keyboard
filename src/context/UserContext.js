// Typing Master
// Last Edit: June 6th, 2018
// Authors: Fahad Alarefi, Ben Martinson

import React, { Component } from 'react'
import axios from 'axios';
import { BrowserRouter, Route, Redirect } from 'react-router-dom'


export const UserContext = React.createContext({
    isLoggedIn: false,
    userInfo: {
      jwtToken: null,
      name: null
    }
  })


// used to direct the user to specified path on come constrains of auth
export const PrivateRoute = ({ component: Component, isLoggedIn: isLoggedIn, shouldBeLoggedIn: shouldBeLoggedIn, redirect: redirect, ...rest }) => (
  <Route {...rest} render={(props) => (
    isLoggedIn === shouldBeLoggedIn
      ? <Component {...props} />
      : <div>
          <Redirect to={{pathname: redirect, state: {errorMsg: true}}} />
      </div>
  )} />
)


// provider for user information and handling auth
export default class UserProvider extends Component {

  defaultState = {
    isLoggedIn: true,
    userInfo: {
      jwtToken: "guest",
      name: "Guest User"
    }
  }

  constructor(props){
    super(props)
    this.logoutHandler = this.logoutHandler.bind(this)
    this.loginHandler = this.loginHandler.bind(this)
    this.state = this.defaultState
  }


  logoutHandler(e){
    // Do nothing for guest mode
  }

  loginHandler(data){
    // Do nothing for guest mode
  }

  componentDidMount() { 
      // Registration system removed
  }

  render() {
    return (
      <UserContext.Provider value={{
        state: this.state,
        loginHandler: this.loginHandler,
        logoutHandler: this.logoutHandler
      }}>
        {this.props.children}
      </UserContext.Provider>
    )

  }
}


// from React official website
// This function takes a component...
export function withAuth(Component) {
  // ...and returns another component...
  return function AuthComponent(props) {
    // ... and renders the wrapped component with the context theme!
    // Notice that we pass through any additional props as well
    return (
      <UserContext.Consumer>
        {auth => <Component {...props} user={auth} />}
      </UserContext.Consumer>
    );
  };
}