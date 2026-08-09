import { useNavigate } from "react-router-dom";

import { useProfile } from "@/features/account/hooks/useProfile";
import { ProfileForm } from "@/features/account/components/ProfileForm";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";

function ProfileFormSkeleton() {
  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4">
        <Skeleton className="h-16 w-16 rounded-full" />
        <Skeleton className="h-8 w-32 rounded-md" />
      </div>
      {Array.from({ length: 4 }).map((_, i) => (
        <div key={i} className="space-y-2">
          <Skeleton className="h-3.5 w-24" />
          <Skeleton className="h-9 w-full" />
        </div>
      ))}
    </div>
  );
}

export default function ProfilePage() {
  const { profile, isLoading, isSaving, saveProfile } = useProfile();
  const navigate = useNavigate();

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold tracking-tight">Profile</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Manage your personal information.
        </p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Personal information</CardTitle>
          <CardDescription>
            This information may be visible to other members of your organization.
          </CardDescription>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <ProfileFormSkeleton />
          ) : (
            <ProfileForm
              profile={profile}
              isSaving={isSaving}
              onSave={saveProfile}
              onCancel={() => navigate("/account")}
            />
          )}
        </CardContent>
      </Card>
    </div>
  );
}
