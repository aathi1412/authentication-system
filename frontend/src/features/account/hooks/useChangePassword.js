import {toast} from "@/components/ui/use-toast";
import {useCallback, useState} from "react";
import apiClient from "../../../lib/axiosClient"


/** Encapsulates the change-password request + its loading/error state. */
export function useChangePassword() {
  const [isSubmitting, setIsSubmitting] = useState(false);

  const changePassword = useCallback(async ({ currentPassword, newPassword }) => {
    setIsSubmitting(true);
    try {
      await apiClient.put("/change-password", );
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
