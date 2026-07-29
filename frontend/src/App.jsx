import {Toaster} from "react-hot-toast";
import {BrowserRouter} from "react-router-dom"
import AppRoutes from "./routes/AppRoutes";

import './App.css'

function App() {


    return (
        <>
            <Toaster/>
            <BrowserRouter>
                <AppRoutes/>
            </BrowserRouter>
        </>

    )
}

export default App
