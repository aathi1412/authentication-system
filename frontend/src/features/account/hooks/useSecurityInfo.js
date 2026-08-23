import {accountApi} from "@/features/account/services/accountApi";
import {useCallback, useEffect, useState} from "react";

/** Loads the read-only security summary shown on the Security page. */
export function useSecurityInfo() {
  const [security, setSecurity] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchSecurity = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const { data } = await accountApi.getSecurityInfo();
      setSecurity(data);
    } catch (err) {
      setError(err);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
      // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchSecurity();
  }, [fetchSecurity]);

  return { security, isLoading, error, refetch: fetchSecurity };
}
