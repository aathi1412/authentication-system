import { z } from "zod";

export const profileSchema = z.object({
  name: z
    .string()
    .trim()
    .min(2, "Full name must be at least 2 characters")
    .max(80, "Full name must be under 80 characters"),
  phone: z
    .string()
    .trim()
    .regex(/^[0-9+\-\s()]{7,15}$/, "Enter a valid phone number")
    .or(z.literal("")),
  bio: z
    .string()
    .trim()
    .max(240, "Bio must be under 240 characters")
    .optional()
    .or(z.literal("")),
});

/** @typedef {z.infer<typeof profileSchema>} ProfileFormValues */
