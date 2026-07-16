import axios from "axios";
import toast from "react-hot-toast";
import {MdOutlineMarkEmailRead} from "react-icons/md";
import useCountDown from "../../customhooks/useCountDown";
import {BASE_PATH_AUTH} from "../../utils/constants";
import AuthSwitch from "./components/AuthSwitch";
import Button from "./components/Button";

export function EmailSent() {

    const {start, isRunning, seconds} = useCountDown();
    const email = localStorage.getItem("email");

    const onClick = async (e) => {
        e.preventDefault();

        await toast.promise(
            axios.post(BASE_PATH_AUTH + "/forgot-password", {email}),
            {
                loading: "Sending reset link...",
                success: "Reset link sent to your email.",
                error: err => err.response?.data?.error?.message || "Something went wrong."
            }
        )
        start()
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <div className="w-full max-w-lg bg-white px-10 py-8 rounded-3xl shadow-2xl text-center">

                <div className="flex gap-4 py-2">
                    <div className="bg-green-100 mt-3 w-10 h-8 flex items-center justify-center rounded-lg">
                        <MdOutlineMarkEmailRead className="text-green-600 "/>
                    </div>

                    <div className="text-start">
                        <h2 className="text-lg font-bold my-3 mr-6">
                            Email sent
                        </h2>
                        <p className="text-gray-500 mb-4">
                            <span className="block mb-3">
                                We've sent a password reset link to: {email}
                            </span>
                            <span className="block">
                                Please check your inbox (or spam folder).
                            </span>
                        </p>

                        <div className="text-gray-500 mb-8">
                            <p className="text-black mb-4">
                                Didn't receive the email?
                            </p>
                            <Button
                                loading={isRunning}
                                onClick={onClick}
                            >
                                {isRunning ?
                                    `Resend reset link (${seconds})`
                                    : "Resend reset link"}
                            </Button>

                            <AuthSwitch
                                path={"/forgot-password"}
                                doAction={"Change Email"}
                                className={"text-sm"}
                            >
                                Wrong email address? {" "}
                            </AuthSwitch>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default EmailSent
