import axios from "axios";
import toast from "react-hot-toast";
import {FaCircleCheck} from "react-icons/fa6";
import {useSearchParams} from "react-router-dom";
import {BASE_PATH_AUTH} from "../../utils/constants";
import useCountDown from "./../../customhooks/useCountDown"
import AuthSwitch from "./AuthSwitch";
import Button from "./Button.jsx";

export function EmailVerification() {

    const email = localStorage.getItem("pendingVerificationEmail");

    const [searchParams] = useSearchParams();
    const status = searchParams.get("status") ?? "sent";

    let title = "";
    let description = "";
    let showResend = false;

    switch (status) {
        case "sent":
            title = "Account Created Successfully!";
            description = `We've sent a verification email to ${email} .Please click the link in the email to activate your account.`;
            showResend = true;
            break;

        case "invalid_or_expired":
            title = "Verification Link  Expired";
            description = "Your verification link has expired. Request a new verification email below.";
            showResend = true;
            break;

        case "verified":
            title = "Email Verified successfully";
            description = "Your email is verified successfully. You can sign in now.";
            break;

    }

    const {start, seconds, isRunning} = useCountDown();
    const handleResend = async () => {

        await toast.promise(
            axios.post(BASE_PATH_AUTH + "/resend-verification-email", {email}),
            {
                loading: "Sending Verification Email...",
                success: "verification email sent successfully.",
                error: (err) => err.response?.data?.message || "Failed to verify email"
            }
        );

        start()
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-slate-100">
            <div className="w-full max-w-xl bg-white px-10 py-8 rounded-3xl shadow-2xl text-center">

                <FaCircleCheck className="text-green-500 text-6xl mx-auto mb-6" />
                <h2 className="text-2xl font-bold mb-3">
                    {title}
                </h2>

                <p className="text-gray-500 mb-8">
                    {description}
                </p>

                {
                    (status === "sent" || status === "expired") &&
                    (<div className="text-gray-500 mb-8">
                        Didn't receive the email?
                        <p>
                            Check your Spam or Junk folder.
                        </p>
                    </div>)
                }


                {showResend && (
                    <Button
                        onClick={handleResend}
                        disabled={isRunning > 0}
                        type="button"
                    >
                        {isRunning > 0
                            ? `Resend Verification Link (${seconds}s)`
                            : "Resend Verification Link"
                        }
                    </Button>
                )}

                <AuthSwitch
                    doAction="Sign in"
                    path="/login"
                >
                    Click to{" "}
                </AuthSwitch>

            </div>
        </div>
    );
}

export default EmailVerification