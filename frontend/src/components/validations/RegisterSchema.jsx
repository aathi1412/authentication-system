import {z} from "zod";

export const RegisterSchema = z.object({
    name: z
        .string()
        .trim()
        .nonempty( "Name is required")
        .min(3, "Name must be at least 3 characters")
        .max(23, "Name must not exceed 23 characters")
        .regex(
            /^[A-Za-z]+(?: [A-Za-z]+)*$/,
            "Name can only contain letters and single spaces"
        ),
    email: z.email("Invalid email address"),
    password: z
        .string()
        .min(8, "Password must be at least 8 characters")
        .max(64, "Password must not exceed 64 characters"),
    acceptedTerms: z.literal(true, {
        error: "You must accept the Terms & Conditions."
    })

})
