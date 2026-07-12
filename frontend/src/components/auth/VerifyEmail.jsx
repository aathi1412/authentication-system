import axios from "axios";
import {useEffect, useState} from "react";
import toast from "react-hot-toast";
import {FaCircleCheck} from "react-icons/fa6";
import {BASE_PATH_AUTH} from "../../utils/constants";
import Button from "./Button.jsx";

export function VerifyEmail() {

    const [timeLeft, setTimeLeft] = useState(60);

    useEffect(() => {
        if (timeLeft === 0) return;

        const timer = setInterval(() => {
            setTimeLeft((prev) => prev - 1);
        }, 1000)

        return () => {clearInterval(timer);}
    }, [timeLeft]);

    const handleResend = async () => {
        const email = localStorage.getItem("pendingVerificationEmail");

        await toast.promise(
            axios.post(BASE_PATH_AUTH + "/resend-verification-email", {email}),
            {
                loading: "Sending Verification Email...",
                success: "verification email sent successfully.",
                error: (err) => err.response?.data?.message || "Failed to verify email"
            }
        );
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-slate-100">
            <div className="w-full max-w-xl bg-white px-10 py-8 rounded-3xl shadow-2xl text-center">

                <FaCircleCheck className="text-green-500 text-6xl mx-auto mb-6" />
                <h2 className="text-2xl font-bold mb-3">
                    Account Created Successfully!
                </h2>

                <p className="text-gray-500 mb-8">
                    We've sent a verification email to <span className="font-semibold text-gray-900 wrap-break-word">address@example.com</span>.
                    Please click the link in the email to activate your account.
                </p>

                <div className="text-gray-500 mb-8">
                    Didn't receive the email?
                    <p>
                        Check your Spam or Junk folder.
                    </p>
                </div>


                <Button
                    onClick={handleResend}
                    disabled={timeLeft > 0}
                    type="button"
                >
                    {timeLeft > 0
                        ? `Resend Verification Link (${timeLeft}s)`
                        : "Resend Verification Link"
                    }
                </Button>

            </div>
        </div>
    );
}

export default VerifyEmail