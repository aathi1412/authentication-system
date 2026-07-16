import {zodResolver} from "@hookform/resolvers/zod";
import axios from "axios";
import {useForm} from "react-hook-form";
import toast from "react-hot-toast";
import {useNavigate, useSearchParams} from "react-router-dom";
import {BASE_PATH_AUTH} from "../../../utils/constants";
import {PasswordResetSchema} from "../validations/authSchema";
import AuthSwitch from "../components/AuthSwitch.jsx";
import Button from "../components/Button.jsx";
import Password from "../components/Password.jsx";

export function ResetPassword() {
    const navigate = useNavigate()
    const {
        register,
        handleSubmit,
        reset,
        formState: {errors}
    } = useForm({
        resolver: zodResolver(PasswordResetSchema)
    })

    const [searchParams] = useSearchParams()
    const token = searchParams.get("token");

    const onSubmit = async (data) => {

        await toast.promise(
            axios.post(BASE_PATH_AUTH + "/reset-password", {token, password: data.password}),
            {
                loading: "Resetting Password...",
                success: "Your password has been changed successfully.",
                error: err => err.response?.data?.message || "Something went wrong"
            }
        )
        reset()
        navigate("/login")
    }

    return (
        <>
            <div className="min-h-screen flex items-center justify-center ">

                <form noValidate onSubmit={handleSubmit(onSubmit)}
                      className="w-full max-w-lg px-15 py-10 shadow-2xl rounded-3xl">

                    <h2 className="text-2xl font-bold mb-2">
                        Reset your password?
                    </h2>
                    <p className="text-gray-500 mb-6">
                        Your new password should be different from previously used.
                    </p>

                    <Password
                        name="password"
                        label="New Password"
                        placeholder="Create a Password"
                        register={register}
                        errors={errors}
                    />
                    <Password
                        name="confirmPassword"
                        label="Confirm your new password"
                        placeholder="Confirm your Password"
                        register={register}
                        errors={errors}
                    />

                    <Button>
                        Confirm
                    </Button>

                    <AuthSwitch
                        doAction="Sign in"
                        path="/login"
                    >
                        Remember your password?{" "}
                    </AuthSwitch>

                </form>
            </div>
        </>
    )
}

export default ResetPassword