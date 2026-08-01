import {Route, Routes} from "react-router-dom"
import EmailSent from "../features/auth/pages/EmailSent";
import EmailVerification from "../features/auth/pages/EmailVerification";
import ForgotPassword from "../features/auth/pages/ForgotPassword";
import Login from "../features/auth/pages/Login";
import PasswordResetSuccess from "../features/auth/pages/PasswordResetSuccess";
import Register from "../features/auth/pages/Register";
import ResetPassword from "../features/auth/pages/ResetPassword";
import Home from "../features/user/pages/Home";
import PATHS from "./paths"

export default function AppRoutes(){
    return (
        <Routes>
            <Route path="/" element={<Home/>}/>
            <Route path={PATHS.USER.HOME} element={<Home/>}/>
            <Route path={PATHS.AUTH.REGISTER} element={<Register/>}/>
            <Route path={PATHS.AUTH.EMAIL_VERIFICATION} element={<EmailVerification/>}/>
            <Route path={PATHS.AUTH.LOGIN} element={<Login/>}/>
            <Route path={PATHS.AUTH.FORGOT_PASSWORD} element={<ForgotPassword/>}/>
            <Route path={PATHS.AUTH.EMAIL_SENT} element={<EmailSent/>}/>
            <Route path={PATHS.AUTH.RESET_PASSWORD} element={<ResetPassword/>}/>
            <Route path={PATHS.AUTH.RESET_SUCCESS} element={<PasswordResetSuccess/>}/>
        </Routes>
    )
}