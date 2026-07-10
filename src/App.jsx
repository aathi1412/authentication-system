import {BrowserRouter, Routes, Route} from "react-router-dom"
import Register from "./components/auth/Register.jsx";
import Login from "./components/auth/Login.jsx";
import {Home} from "./components/Home.jsx";


import './App.css'
import TermsAndConditions from "./utils/TermsAndConditions.jsx";
import ForgotPassword from "./components/auth/ForgotPassword.jsx";
import {ResetPassword} from "./components/auth/ResetPassword.jsx";
import SuccessMessage from "./components/auth/SuccessMessage.jsx";
function App() {


  return (
    <BrowserRouter>
      <Routes>
          <Route path="/" element={<Home/>}/>
          <Route path="/register" element={<Register/>}/>
          <Route path="/login" element={<Login/>}/>
          <Route path="/TermsAndConditions" element={<TermsAndConditions/>} />
          <Route path="/forgot-password" element={<ForgotPassword/>} />
          <Route path="/reset-password" element={<ResetPassword/>} />
          <Route path="/reset-password/success" element={<SuccessMessage/>}/>
      </Routes>
    </BrowserRouter>
  )
}

export default App
