const Auth = "auth";
const USER = "user";

const PATHS = {
    AUTH: {
        LOGIN: `/${Auth}/login`,
        REGISTER: `/${Auth}/register`,
        FORGOT_PASSWORD: `/${Auth}/forgot-password`,
        RESET_PASSWORD: `/${Auth}/register-password`,
        EMAIL_VERIFICATION: `/${Auth}/email-verification`,
        EMAIL_SENT: `/${Auth}/forgot-password/email-sent`,
        RESET_SUCCESS: `/${Auth}/reset-password/success`,
    },

    USER: {
        HOME: `/${USER}/me`,
    }
}

export default PATHS