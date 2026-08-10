import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";
import {Badge} from "@/components/ui/badge";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Skeleton} from "@/components/ui/skeleton";

import {useProfile} from "@/features/account/hooks/useProfile";
import {useSecurityInfo} from "@/features/account/hooks/useSecurityInfo";
import {ArrowRight, BadgeCheck, ScrollText, ShieldAlert, ShieldCheck, UserRound,} from "lucide-react";
import {Link} from "react-router-dom";
import PATHS from "./paths"

function getInitials(name = "") {
  return name
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase())
    .join("");
}

const SHORTCUTS = [
  {
    to: PATHS.USER.PROFILE,
    icon: UserRound,
    title: "Profile",
    description: "Update your name, phone number, and bio",
  },
  {
    to: PATHS.USER.SECURITY,
    icon: ShieldCheck,
    title: "Security",
    description: "Change your password and review account status",
  },
  {
    to: PATHS.USER.ACTIVITY,
    icon: ScrollText,
    title: "Activity logs",
    description: "See recent logins and account changes",
  },
];

export default function OverviewPage() {
  const { profile, isLoading: profileLoading } = useProfile();
  const { security, isLoading: securityLoading } = useSecurityInfo();

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold tracking-tight">Overview</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          A quick summary of your account.
        </p>
      </div>

      <Card>
        <CardContent className="flex flex-col gap-4 p-5 sm:flex-row sm:items-center sm:justify-between">
          <div className="flex items-center gap-4">
            {profileLoading ? (
              <Skeleton className="h-14 w-14 rounded-full" />
            ) : (
              <Avatar className="h-14 w-14 border border-border">
                <AvatarImage src={profile?.profileImage} alt={profile?.name} />
                <AvatarFallback className="text-base">
                  {getInitials(profile?.name)}
                </AvatarFallback>
              </Avatar>
            )}
            <div>
              {profileLoading ? (
                <>
                  <Skeleton className="h-4 w-32" />
                  <Skeleton className="mt-2 h-3.5 w-44" />
                </>
              ) : (
                <>
                  <p className="text-sm font-semibold">{profile?.name}</p>
                  <p className="text-sm text-muted-foreground">{profile?.email}</p>
                </>
              )}
            </div>
          </div>

          {!securityLoading && security && (
            <div className="flex items-center gap-2">
              {security.emailVerified ? (
                <Badge variant="success">
                  <BadgeCheck className="h-3 w-3" />
                  Email verified
                </Badge>
              ) : (
                <Badge variant="warning">
                  <ShieldAlert className="h-3 w-3" />
                  Email unverified
                </Badge>
              )}
              <Badge variant="secondary">{security.role}</Badge>
            </div>
          )}
        </CardContent>
      </Card>

      <div className="grid gap-4 sm:grid-cols-3">
        {SHORTCUTS.map(({ to, icon: Icon, title, description }) => (
          <Link key={to} to={to}>
            <Card className="h-full transition-colors hover:border-foreground/20 hover:bg-secondary/40">
              <CardHeader className="pb-2">
                <div className="flex items-center justify-between">
                  <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-muted">
                    <Icon className="h-4 w-4 text-muted-foreground" />
                  </div>
                  <ArrowRight className="h-4 w-4 text-muted-foreground" />
                </div>
                <CardTitle className="pt-2">{title}</CardTitle>
              </CardHeader>
              <CardContent className="pt-0 text-sm text-muted-foreground">
                {description}
              </CardContent>
            </Card>
          </Link>
        ))}
      </div>
    </div>
  );
}
