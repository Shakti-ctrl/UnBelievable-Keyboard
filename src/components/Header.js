// Typing Master
// Last Edit: June 6th, 2018
// Authors: Fahad Alarefi, Ben Martinson

import React, { Component } from 'react'
import { NavLink } from 'react-router-dom'

class Header extends Component {

  constructor(props) {
    super(props);
  }

 render() {
  // an error handler 
  let showError = false
  if(this.props.location.state && this.props.location.state.errorMsg){
    showError = true
  }
   return (
    <div className="header">
    {showError && <div className="alert alert-info" role="alert">You must login before accessing this page</div>}
     <nav className="navbar navbar-expand-lg navbar-light bg-light">
      <h4>Typing Master</h4>
      <div className="collapse navbar-collapse" id="navbarSupportedContent">
        <ul className="navbar-nav mr-auto">
          <li className="nav-item">
            <NavLink to="/" exact activeClassName="active" className="nav-link">Practice</NavLink>
          </li>
          <li className="nav-item">
            <NavLink to="/coach" activeClassName="active" className="nav-link">Coach</NavLink>
          </li>
          <li className="nav-item">
            <NavLink to="/user/train" activeClassName="active" className="nav-link">Train</NavLink>
          </li>
          <li className="nav-item">
            <NavLink to="/user/data" activeClassName="active" className="nav-link">Track</NavLink>
          </li>
          <li className="nav-item">
            <NavLink to="/about" activeClassName="active" className="nav-link">About</NavLink>
          </li>
        </ul>

        <div className="inline greeting">
          {/* Auth buttons removed for immediate access */}
        </div>
      </div>
    </nav>
    </div>
   )
 }
}

export default Header;
