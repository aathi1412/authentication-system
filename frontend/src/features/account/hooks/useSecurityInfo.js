import { useCallback, useEffect, useState } from "react";

import { accountApi } from "@/features/account/services/accountApi";

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
    fetchSecurity();
  }, [fetchSecurity]);

  return { security, isLoading, error, refetch: fetchSecurity };
}
