import {z} from "zod";


export const NameSchema = z
        .string()
        .trim()
        .nonempty( "Name is required")
        .min(3, "Name must be at least 3 characters")
        .max(23, "Name must not exceed 23 characters")
        .regex(
            /^[A-Za-z]+(?: [A-Za-z]+)*$/,
            "Name can only contain letters and single spaces"
        );

export const EmailSchema = z
    .string()
    .trim()
    .nonempty("Email is required")
    .pipe(
        z.email("Invalid email address")
    );

export const PasswordSchema = z
        .string()
        .min(8, "Password must be at least 8 characters")
        .max(64, "Password must not exceed 64 characters");

export const RegisterSchema = z.object({
    name: NameSchema,
    email: EmailSchema,
    password: PasswordSchema,
    acceptedTerms: z.literal(true, {
        error: "You must accept the Terms & Conditions."
    })
})

export const LoginSchema = z.object({
    email: EmailSchema,
    password: PasswordSchema
})

export const ForgotPasswordSchema = z.object({
    email: EmailSchema
})

export const PasswordResetSchema = z
    .object({
        password: PasswordSchema,
        confirmPassword: PasswordSchema
    })
    .refine(
        (data ) => data.password === data.confirmPassword,
            {
                path: ["confirmPassword"],
                message: "Passwords do not match"
            }
    );
