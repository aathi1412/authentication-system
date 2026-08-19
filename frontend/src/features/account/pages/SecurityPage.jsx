import {Badge} from "@/components/ui/badge";
import {Card, CardContent, CardDescription, CardHeader, CardTitle,} from "@/components/ui/card";
import {Skeleton} from "@/components/ui/skeleton";
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs";
import {AccountCard} from "@/features/account/components/AccountCard";
import {PasswordForm} from "@/features/account/components/PasswordForm";

import {useChangePassword} from "@/features/account/hooks/useChangePassword";
import {useSecurityInfo} from "@/features/account/hooks/useSecurityInfo";
import {CalendarDays, Clock, Lock, LockOpen, MailCheck, MailWarning, ShieldAlert, ShieldCheck,} from "lucide-react";

function formatDateTime(iso) {
  if (!iso) return "—";
  return new Date(iso).toLocaleString(undefined, {
    month: "short",
    day: "numeric",
    year: "numeric",
    hour: "numeric",
    minute: "2-digit",
  });
}

function SecurityInfoGrid({ security, isLoading }) {
  if (isLoading) {
    return (
      <div className="grid gap-3 sm:grid-cols-2">
        {Array.from({ length: 6 }).map((_, i) => (
          <Skeleton key={i} className="h-[68px] rounded-xl" />
        ))}
      </div>
    );
  }

  if (!security) return null;

  return (
    <div className="grid gap-3 sm:grid-cols-2">
      <AccountCard
        icon={security.emailVerified ? MailCheck : MailWarning}
        label="Email verified"
        value={
          <Badge variant={security.emailVerified ? "success" : "warning"}>
            {security.emailVerified ? "Verified" : "Unverified"}
          </Badge>
        }
      />
      <AccountCard
        icon={ShieldCheck}
        label="Role"
        value={<Badge variant="secondary">{security.role}</Badge>}
      />
      <AccountCard
        icon={security.accountLocked ? Lock : LockOpen}
        label="Account locked"
        value={
          <Badge variant={security.accountLocked ? "destructive" : "success"}>
            {security.accountLocked ? "Locked" : "Active"}
          </Badge>
        }
      />
      <AccountCard
        icon={ShieldAlert}
        label="Failed login attempts"
        value={security.failedAttempts}
      />
      <AccountCard
        icon={Clock}
        label="Last login"
        value={formatDateTime(security.lastLogin)}
      />
      <AccountCard
        icon={CalendarDays}
        label="Created at"
        value={formatDateTime(security.createdAt)}
      />
    </div>
  );
}

export default function SecurityPage() {
  const { changePassword, isSubmitting } = useChangePassword();
  const { security, isLoading: securityLoading } = useSecurityInfo();

    console.log(changePassword);
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold tracking-tight">Security</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Manage your password and review your account's security status.
        </p>
      </div>

      <Tabs defaultValue="password">
        <TabsList>
          <TabsTrigger value="password">Change password</TabsTrigger>
          <TabsTrigger value="info">Security information</TabsTrigger>
        </TabsList>

        <TabsContent value="password">
          <Card>
            <CardHeader>
              <CardTitle>Change password</CardTitle>
              <CardDescription>
                Choose a strong password you don't use anywhere else.
              </CardDescription>
            </CardHeader>
            <CardContent>
              <PasswordForm isSubmitting={isSubmitting} onSubmit={changePassword} />
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="info">
          <Card>
            <CardHeader>
              <CardTitle>Security information</CardTitle>
              <CardDescription>
                A read-only snapshot of your account's current security state.
              </CardDescription>
            </CardHeader>
            <CardContent>
              <SecurityInfoGrid security={security} isLoading={securityLoading} />
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
