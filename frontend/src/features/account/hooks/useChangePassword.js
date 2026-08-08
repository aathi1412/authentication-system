import { useCallback, useState } from "react";

import { accountApi } from "@/features/account/services/accountApi";
import { toast } from "@/components/ui/use-toast";

/** Encapsulates the change-password request + its loading/error state. */
export function useChangePassword() {
  const [isSubmitting, setIsSubmitting] = useState(false);

  const changePassword = useCallback(async ({ currentPassword, newPassword }) => {
    setIsSubmitting(true);
    try {
      await accountApi.changePassword({ currentPassword, newPassword });
      toast({
        variant: "success",
        title: "Password updated",
        description: "Use your new password next time you sign in.",
      });
      return true;
    } catch (err) {
      toast({
        variant: "destructive",
        title: "Couldn't update password",
        description:
          err.response?.data?.message ||
          "Check your current password and try again.",
      });
      throw err;
    } finally {
      setIsSubmitting(false);
    }
  }, []);

  return { changePassword, isSubmitting };
}
