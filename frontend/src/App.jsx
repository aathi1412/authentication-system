import {Toaster as CustomToaster} from "@/components/ui/toaster";

import {router} from "@/routes/router";
import {Toaster} from "react-hot-toast";
import {RouterProvider} from "react-router-dom"

function App() {

    return (
        <>
            <Toaster/>
            <RouterProvider router={router} />
            <CustomToaster />
        </>

    )
}

export default App
