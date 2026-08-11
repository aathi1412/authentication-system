import {Spinner} from "@/components/Spinner";
import {lazy, Suspense} from "react";
import {createBrowserRouter, Navigate} from "react-router-dom";
import PATHS from "./paths"

// Account
const AccountLayout = lazy(() =>
    import("@/features/account/pages/AccountLayout")
);
const OverviewPage = lazy(() =>
    import("@/features/account/pages/OverviewPage")
);
const ProfilePage = lazy(() =>
    import("@/features/account/pages/ProfilePage")
);
const SecurityPage = lazy(() =>
    import("@/features/account/pages/SecurityPage")
);
const ActivityPage = lazy(() =>
    import("@/features/account/pages/ActivityPage")
);

// Auth
const Login = lazy(() =>
    import("@/features/auth/pages/Login")
);
const Register = lazy(() =>
    import("@/features/auth/pages/Register")
);
const EmailVerification = lazy(() =>
    import("@/features/auth/pages/EmailVerification")
);
const ForgotPassword = lazy(() =>
    import("@/features/auth/pages/ForgotPassword")
);
const EmailSent = lazy(() =>
    import("@/features/auth/pages/EmailSent")
);
const ResetPassword = lazy(() =>
    import("@/features/auth/pages/ResetPassword")
);
const PasswordResetSuccess = lazy(() =>
    import("@/features/auth/pages/PasswordResetSuccess")
);

// Other
const NotFoundPage = lazy(() =>
    import("@/routes/NotFoundPage")
);

function PageFallback() {
    return (
        <div className="flex min-h-[50vh] items-center justify-center">
            <Spinner size={24} className="text-muted-foreground" />
        </div>
    );
}

function withSuspense(element) {
    return (
        <Suspense fallback={<PageFallback />}>
            {element}
        </Suspense>
    );
}

export const router = createBrowserRouter([
        {
            path: "/",
            element: <Navigate to={PATHS.USER.HOME} replace />,
        },

        // Auth
        {
            path: PATHS.AUTH.LOGIN,
            element: withSuspense(<Login />),
        },
        {
            path: PATHS.AUTH.REGISTER,
            element: withSuspense(<Register />),
        },
        {
            path: PATHS.AUTH.EMAIL_VERIFICATION,
            element: withSuspense(<EmailVerification />),
        },
        {
            path: PATHS.AUTH.FORGOT_PASSWORD,
            element: withSuspense(<ForgotPassword />),
        },
        {
            path: PATHS.AUTH.EMAIL_SENT,
            element: withSuspense(<EmailSent />),
        },
        {
            path: PATHS.AUTH.RESET_PASSWORD,
            element: withSuspense(<ResetPassword />),
        },
        {
            path: PATHS.AUTH.RESET_SUCCESS,
            element: withSuspense(<PasswordResetSuccess />),
        },

        // Account
        {
            path: PATHS.USER.HOME,
            element: withSuspense(<AccountLayout />),
            children: [
                {
                    index: true,
                    element: withSuspense(<OverviewPage />),
                },
                {
                    path: "profile",
                    element: withSuspense(<ProfilePage />),
                },
                {
                    path: "security",
                    element: withSuspense(<SecurityPage />),
                },
                {
                    path: "activity",
                    element: withSuspense(<ActivityPage />),
                },
            ],
        },

        // 404
        {
            path: "*",
            element: withSuspense(<NotFoundPage />),
        },
    ],
    {
        future: {
            v7_startTransition: true,
        }
    }
);