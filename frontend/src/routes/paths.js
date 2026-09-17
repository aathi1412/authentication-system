const Auth = "auth";
const ACCOUNT = "/user/account";

const PATHS = {
    AUTH: {
        LOGIN: `/${Auth}/login`,
        REGISTER: `/${Auth}/register`,
        FORGOT_PASSWORD: `/${Auth}/forgot-password`,
        RESET_PASSWORD: `/${Auth}/reset-password`,
        EMAIL_VERIFICATION: `/${Auth}/email-verification`,
        EMAIL_SENT: `/${Auth}/forgot-password/email-sent`,
        RESET_SUCCESS: `/${Auth}/reset-password/success`,
    },

    USER: {
        HOME: `${ACCOUNT}`,
        PROFILE: `${ACCOUNT}/profile`,
        SECURITY: `${ACCOUNT}/security`,
        ACTIVITY: `${ACCOUNT}/activity`,
    },

    ADMIN: {
        HOME: '/admin',
        USER: "/admin/users",
        ACTIVITY: "/admin/activity",
        SETTINGS: "/admin/settings"
    }
}

export default PATHS