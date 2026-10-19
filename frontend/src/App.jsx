import { Toaster as CustomToaster } from "@/components/ui/toaster";
import { router } from "@/routes/router";
import { RouterProvider } from "react-router-dom";

function App() {
  return (
    <>
      <RouterProvider router={router} />
      <CustomToaster />
    </>
  );
}

export default App;
