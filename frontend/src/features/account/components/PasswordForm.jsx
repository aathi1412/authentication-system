import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Eye, EyeOff, Check, X } from "lucide-react";

import {
  passwordSchema,
  passwordRuleList,
  getPasswordStrength,
} from "@/features/account/schemas/passwordSchema";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Progress } from "@/components/ui/progress";
import { Spinner } from "@/components/Spinner";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";

const STRENGTH_COLOR = [
  "bg-destructive",
  "bg-destructive",
  "bg-warning",
  "bg-warning",
  "bg-success",
];

function PasswordInput({ field, placeholder }) {
  const [visible, setVisible] = useState(false);
  return (
    <div className="relative">
      <Input
        type={visible ? "text" : "password"}
        placeholder={placeholder}
        autoComplete="new-password"
        className="pr-10"
        {...field}
      />
      <button
        type="button"
        onClick={() => setVisible((v) => !v)}
        className="absolute right-2.5 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
        aria-label={visible ? "Hide password" : "Show password"}
        tabIndex={-1}
      >
        {visible ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
      </button>
    </div>
  );
}

function PasswordStrengthMeter({ value }) {
  const { percent, label, passed } = getPasswordStrength(value);
  const colorIndex = Math.max(0, Math.min(4, Math.round(percent / 25)));

  return (
    <div className="space-y-2.5 rounded-lg border border-border bg-muted/40 p-3">
      <div className="flex items-center justify-between">
        <Progress
          value={percent}
          className="mr-3 h-1.5 flex-1"
          indicatorClassName={STRENGTH_COLOR[colorIndex]}
        />
        <span className="w-16 shrink-0 text-right text-xs font-medium text-muted-foreground">
          {label}
        </span>
      </div>
      <ul className="grid grid-cols-1 gap-1 sm:grid-cols-2">
        {passwordRuleList.map((rule, i) => (
          <li
            key={rule.label}
            className={`flex items-center gap-1.5 text-xs ${
              passed[i] ? "text-success" : "text-muted-foreground"
            }`}
          >
            {passed[i] ? (
              <Check className="h-3 w-3 shrink-0" />
            ) : (
              <X className="h-3 w-3 shrink-0" />
            )}
            {rule.label}
          </li>
        ))}
      </ul>
    </div>
  );
}

/**
 * @param {{ isSubmitting: boolean, onSubmit: (values: any) => Promise<void> }} props
 */
export function PasswordForm({ isSubmitting, onSubmit }) {
  const form = useForm({
    resolver: zodResolver(passwordSchema),
    defaultValues: { currentPassword: "", newPassword: "", confirmPassword: "" },
  });

  const newPasswordValue = form.watch("newPassword");

  const handleSubmit = async (values) => {
    await onSubmit(values);
    form.reset();
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
        <FormField
          control={form.control}
          name="currentPassword"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Current password</FormLabel>
              <FormControl>
                <PasswordInput field={field} placeholder="Enter current password" />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="newPassword"
          render={({ field }) => (
            <FormItem>
              <FormLabel>New password</FormLabel>
              <FormControl>
                <PasswordInput field={field} placeholder="Enter new password" />
              </FormControl>
              <PasswordStrengthMeter value={newPasswordValue} />
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="confirmPassword"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Confirm new password</FormLabel>
              <FormControl>
                <PasswordInput field={field} placeholder="Re-enter new password" />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="flex justify-end pt-2">
          <Button type="submit" disabled={isSubmitting}>
            {isSubmitting && <Spinner className="text-primary-foreground" />}
            {isSubmitting ? "Updating..." : "Update password"}
          </Button>
        </div>
      </form>
    </Form>
  );
}
