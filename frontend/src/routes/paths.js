const Auth = "/auth";
const USER = "/user";

export const Paths = {
    Auth: {
        LOGIN: `/${Auth}/login`,
        REGISTER: `/${Auth}/register`,
        FORGOT_PASSWORD: `/${Auth}/forgot-password`,
        REGISTER_PASSWORD: `/${Auth}/register-password`,
        EMAIL_VERIFICATION: `/${Auth}/email-verification`,
        EMAIL_SENT: `/${Auth}/forgot-password/email-sent`,
        RESET_SUCCESS: `/${Auth}/reset-password/success`,
    },

    USER: {
        HOME: `/${USER}/me`,
    }


}