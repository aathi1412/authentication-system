import {Toaster as customToaster} from "@/components/ui/toaster";

import {router} from "@/routes/router";
import {Toaster} from "react-hot-toast";
import {BrowserRouter, RouterProvider} from "react-router-dom"
import AppRoutes from "./routes/AppRoutes";

function App() {


    return (
        <>
            <Toaster/>
            <BrowserRouter>
                <AppRoutes/>
            </BrowserRouter>
            <RouterProvider router={router} />
            <customToaster />
        </>

    )
}

export default App
