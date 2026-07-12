import {Toaster} from "react-hot-toast";
import {BrowserRouter, Route, Routes} from "react-router-dom"
import ForgotPassword from "./components/auth/ForgotPassword.jsx";
import Login from "./components/auth/Login.jsx";
import Register from "./components/auth/Register.jsx";
import {ResetPassword} from "./components/auth/ResetPassword.jsx";
import SuccessMessage from "./components/auth/SuccessMessage.jsx";
import VerifyEmail from "./components/auth/VerifyEmail";
import {Home} from "./components/Home.jsx";


import './App.css'
import TermsAndConditions from "./utils/TermsAndConditions.jsx";

function App() {


  return (
      <>
          <Toaster />
          <BrowserRouter>
              <Routes>
                  <Route path="/" element={<Home/>}/>
                  <Route path="/register" element={<Register/>}/>
                  <Route path="/register/verify-email" element={<VerifyEmail/>}/>
                  <Route path="/login" element={<Login/>}/>
                  <Route path="/TermsAndConditions" element={<TermsAndConditions/>} />
                  <Route path="/forgot-password" element={<ForgotPassword/>} />
                  <Route path="/reset-password" element={<ResetPassword/>} />
                  <Route path="/reset-password/success" element={<SuccessMessage/>}/>
              </Routes>
          </BrowserRouter>
      </>

  )
}

export default App
