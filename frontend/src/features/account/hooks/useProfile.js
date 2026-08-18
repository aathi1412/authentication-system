import {toast} from "@/components/ui/use-toast";
import {useCallback, useEffect, useState} from "react";
import apiClient from "../../../lib/axiosClient"

/**
 * Loads the current user's profile and exposes a save() action used by
 * ProfilePage / ProfileForm. Keeps loading/saving state separate so the
 * page can show a skeleton on first load but a button spinner on save.
 */
export function useProfile() {
  const [profile, setProfile] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState(null);

  const fetchProfile = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const { data } = await apiClient.get("/users/me");
      setProfile(data);
    } catch (err) {
      setError(err);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchProfile();
  }, [fetchProfile]);

  const saveProfile = useCallback(async (values) => {
    setIsSaving(true);
    try {
      const { data } = await apiClient.put("/users/me", values);
      setProfile(data);
      toast({
        variant: "success",
        title: "Profile updated",
        description: "Your changes have been saved.",
      });
      return data;
    } catch (err) {
      toast({
        variant: "destructive",
        title: "Couldn't save changes",
        description:
          err.response?.data?.message || "Please try again in a moment.",
      });
      throw err;
    } finally {
      setIsSaving(false);
    }
  }, []);

  return { profile, isLoading, isSaving, error, saveProfile, refetch: fetchProfile };
}
