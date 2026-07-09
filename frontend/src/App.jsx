import {BrowserRouter, Routes, Route} from "react-router-dom"
import Register from "./components/Register.jsx";
import Login from "./components/Login.jsx";
import {Home} from "./components/Home.jsx";


import './App.css'
import TermsAndConditions from "./utils/TermsAndConditions.jsx";
function App() {


  return (
    <BrowserRouter>
      <Routes>
          <Route path="/" element={<Home/>}/>
          <Route path="/register" element={<Register/>}/>
          <Route path="/login" element={<Login/>}/>
          <Route path="/TermsAndConditions" element={<TermsAndConditions/>} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
