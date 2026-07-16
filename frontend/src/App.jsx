import {Toaster} from "react-hot-toast";
import {BrowserRouter, Route, Routes} from "react-router-dom"
import EmailSent from "./features/auth/EmailSent";
import EmailVerification from "./features/auth/EmailVerification";
import ForgotPassword from "./features/auth/pages/ForgotPassword";
import Login from "./features/auth/pages/Login";
import PasswordResetSuccess from "./features/auth/PasswordResetSuccess";
import Register from "./features/auth/pages/Register";
import ResetPassword from "./features/auth/pages/ResetPassword";
import Home from "./pages/home/Home";
import './App.css'

function App() {


    return (
        <>
            <Toaster/>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Home/>}/>
                    <Route path="/auth/register" element={<Register/>}/>
                    <Route path="/auth/email-verification" element={<EmailVerification/>}/>
                    <Route path="/auth/login" element={<Login/>}/>
                    <Route path="/auth/forgot-password" element={<ForgotPassword/>}/>
                    <Route path="/auth/forgot-password/email-sent" element={<EmailSent/>}/>
                    <Route path="/auth/reset-password" element={<ResetPassword/>}/>
                    <Route path="/auth/reset-password/success" element={<PasswordResetSuccess/>}/>
                </Routes>
            </BrowserRouter>
        </>

    )
}

export default App
