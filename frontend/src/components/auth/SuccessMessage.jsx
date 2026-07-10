import { FaCircleCheck } from "react-icons/fa6";
import Button from "./Button.jsx";

export function SuccessMessage() {
    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <div className="w-full max-w-lg bg-white px-10 py-8 rounded-3xl shadow-2xl text-center">

                <FaCircleCheck className="text-green-500 text-6xl mx-auto mb-4" />
                <h2 className="text-2xl font-bold mb-3">
                    Password Reset Successful 🎉
                </h2>

                <p className="text-gray-500 mb-8">
                    Your password has been successfully reset.
                    Click the button below to sign in with your new password.
                </p>

                <Button text="Sign In" />
            </div>
        </div>
    );
}

export default SuccessMessage