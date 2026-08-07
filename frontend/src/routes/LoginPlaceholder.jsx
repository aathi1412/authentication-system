import { KeyRound } from "lucide-react";

import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";

/**
 * Placeholder so logout / session-expiry redirects have somewhere to land.
 * Swap this out for SecureAuth's real authentication module.
 */
export default function LoginPlaceholder() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-background px-4">
      <Card className="w-full max-w-sm">
        <CardHeader className="items-center text-center">
          <div className="mb-2 flex h-10 w-10 items-center justify-center rounded-lg bg-primary text-primary-foreground">
            <KeyRound className="h-5 w-5" />
          </div>
          <CardTitle>You've been signed out</CardTitle>
          <CardDescription>
            This is a placeholder — connect SecureAuth's sign-in flow here.
          </CardDescription>
        </CardHeader>
        <CardContent className="text-center text-sm text-muted-foreground">
          <a href="/account" className="font-medium text-foreground underline underline-offset-4">
            Return to account
          </a>
        </CardContent>
      </Card>
    </div>
  );
}
