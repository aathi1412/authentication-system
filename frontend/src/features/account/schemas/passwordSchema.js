import { z } from "zod";

const passwordRules = {
  minLength: { test: (v) => v.length >= 8, label: "At least 8 characters" },
  uppercase: { test: (v) => /[A-Z]/.test(v), label: "One uppercase letter" },
  lowercase: { test: (v) => /[a-z]/.test(v), label: "One lowercase letter" },
  number: { test: (v) => /[0-9]/.test(v), label: "One number" },
  special: {
    test: (v) => /[^A-Za-z0-9]/.test(v),
    label: "One special character",
  },
};

export const passwordRuleList = Object.values(passwordRules);

/**
 * Scores a candidate password against the rule set.
 * @param {string} value
 * @returns {{ score: number, total: number, passed: boolean[], label: string, percent: number }}
 */
export function getPasswordStrength(value = "") {
  const passed = passwordRuleList.map((rule) => rule.test(value));
  const score = passed.filter(Boolean).length;
  const total = passwordRuleList.length;
  const percent = value.length === 0 ? 0 : Math.round((score / total) * 100);

  const labels = ["Very weak", "Weak", "Fair", "Good", "Strong"];
  const label = value.length === 0 ? "" : labels[Math.min(score, labels.length - 1)];

  return { score, total, passed, label, percent };
}

const strongPassword = z
  .string()
  .min(8, "Password must be at least 8 characters")
  .regex(/[A-Z]/, "Add at least one uppercase letter")
  .regex(/[a-z]/, "Add at least one lowercase letter")
  .regex(/[0-9]/, "Add at least one number")
  .regex(/[^A-Za-z0-9]/, "Add at least one special character");

export const passwordSchema = z
  .object({
    currentPassword: z.string().min(1, "Current password is required"),
    newPassword: strongPassword,
    confirmPassword: z.string().min(1, "Please confirm your new password"),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: "Passwords do not match",
    path: ["confirmPassword"],
  })
  .refine((data) => data.newPassword !== data.currentPassword, {
    message: "New password must be different from the current password",
    path: ["newPassword"],
  });

/** @typedef {z.infer<typeof passwordSchema>} PasswordFormValues */
