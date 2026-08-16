import {zodResolver} from "@hookform/resolvers/zod";
import {useState} from "react";
import {useForm} from "react-hook-form";
import toast from "react-hot-toast";
import {useNavigate} from "react-router-dom";
import {registerUser} from "../../../lib/authApi";
import PATHS from "../../../routes/paths"
import AuthSwitch from "../components/AuthSwitch";
import Button from "../components/Button";
import Email from "../components/Email";
import Name from "../components/Name";
import Password from "../components/Password";
import {RegisterSchema} from "../validations/authSchema";
import TermsAndConditions from "./TermsAndConditions";

export function Register() {

    const [loading, setLoading] = useState(false)
    const navigate = useNavigate()

    const {
        register,
        handleSubmit,
        formState: {errors},
    } = useForm({
        resolver: zodResolver(RegisterSchema)
    })

    const onSubmit = async (data) => {
        try {
            setLoading(true)
            const response = await toast.promise(
                registerUser(data),
                {
                    loading: "Creating account...",
                    success: "Registration successful! Verify your account.",
                    error: (err) => err.response?.data?.message || "Something went wrong."
                }
            );
            console.log(response.data);
            localStorage.setItem("pendingVerificationEmail", data.email);
            navigate(PATHS.AUTH.EMAIL_VERIFICATION);
        } finally {
            setLoading(false)
        }

    }

    return (
        <>
            <div className="min-h-screen flex flex-col items-center justify-center ">

                <form noValidate onSubmit={handleSubmit(onSubmit)}
                      className="py-16 px-12 shadow-2xl rounded-3xl">

                    <div>
                        <h2 className="font-bold text-2xl m-3 p-2">
                            Create your account
                        </h2>
                    </div>

                    <Name
                        register={register}
                        errors={errors}
                    />
                    <Email
                        register={register}
                        errors={errors}
                    />
                    <Password
                        label="Password"
                        placeholder="create a Password"
                        register={register}
                        errors={errors}
                    />

                    <TermsAndConditions
                        register={register}
                        errors={errors}
                    />

                    <Button
                        loading={loading}
                    >
                        Sign up
                    </Button>

                    <AuthSwitch
                        doAction="Sign in"
                        path={"/auth/login"}
                    >
                        Already have an account?
                    </AuthSwitch>

                </form>
            </div>
        </>
    )
}

export default Register;