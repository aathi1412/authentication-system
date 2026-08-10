import {zodResolver} from "@hookform/resolvers/zod";
import {useForm} from "react-hook-form";
import toast from "react-hot-toast";
import {useNavigate} from "react-router-dom";
import {forgotPassword} from "../../../api/authApi"
import PATHS from "../../../routes/paths"
import AuthSwitch from "../components/AuthSwitch.jsx";
import Button from "../components/Button.jsx";
import Email from "../components/Email.jsx";
import {ForgotPasswordSchema} from "../validations/authSchema";

export function ForgotPassword() {

    const navigate = useNavigate();

    const {
        register,
        handleSubmit,
        reset,
        formState: {errors}
    } = useForm({
        resolver: zodResolver(ForgotPasswordSchema)
    })

    const onSubmit = async (data) => {
        localStorage.setItem("email", data.email);
        await toast.promise(
            forgotPassword(data),
            {
                loading: "Sending reset link...",
                success: "Reset link sent to your email.",
                error: err => err.response?.data?.error?.message || "Something went wrong."
            }
        );
        reset()
        navigate(PATHS.AUTH.EMAIL_SENT)
        console.log("sent")
    }

    return (
        <>
            <div className="min-h-screen flex flex-col items-center justify-center ">

                <form noValidate onSubmit={handleSubmit(onSubmit)}
                      className="w-full max-w-lg px-12 py-16 shadow-2xl rounded-3xl">

                    <h2 className="text-2xl font-bold mb-2">Forgot your password?</h2>
                    <p className="text-gray-500 mb-6">Enter your email and we’ll send you a link to reset your
                        password.</p>

                    <Email
                        register={register}
                        errors={errors}
                    />

                    <Button>
                        Confirm
                    </Button>

                    <AuthSwitch
                        doAction="Sign in"
                        path={PATHS.AUTH.LOGIN}
                    >
                        Remember your password? {" "}
                    </AuthSwitch>

                </form>
            </div>
        </>
    )
}

export default ForgotPassword