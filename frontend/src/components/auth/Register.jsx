import {zodResolver} from "@hookform/resolvers/zod";
import axios from "axios";
import {useState} from "react";
import {useForm} from "react-hook-form";
import toast from "react-hot-toast";
import {useNavigate} from "react-router-dom";
import {BASE_PATH_AUTH} from "../../utils/constants";
import TacPopup from "../../utils/TacPopup";
import {RegisterSchema} from "../validations/RegisterSchema";

import AuthSwitch from "./AuthSwitch";
import Button from "./Button";
import Email from "./Email";
import Name from "./Name";
import Password from "./Password";

export function Register(){

    const [loading, setLoading] = useState(false)
    const navigate = useNavigate()

    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm({
        resolver: zodResolver(RegisterSchema)
    })

    const onSubmit = async (data) => {
        try {
            setLoading(true)
            const response = await toast.promise(
                axios.post(BASE_PATH_AUTH + "/register", data),
                {
                        loading: "Creating account...",
                        success: "Registration successful! Sign in to continue.",
                        error: (err) => err.response?.data?.message || "Something went wrong"
                    }
            );
            console.log(response.data);
            navigate("/login");
        }finally {
            setLoading(false)
        }

    }

    return(
        <>
            <div className="min-h-screen flex flex-col items-center justify-center ">

                <form noValidate onSubmit={handleSubmit(onSubmit)}
                    className="px-15 py-10 shadow-2xl rounded-3xl">

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

                    <TacPopup
                        register={register}
                        errors={errors}
                    />

                    <Button
                        text="Sign up"
                        loading={loading}
                    />

                    <AuthSwitch
                        text="Already have an account? "
                        doAction="Sign in"
                        path="/login"
                    />

                </form>
            </div>
        </>
    )
}

export default Register;