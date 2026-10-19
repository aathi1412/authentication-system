import { useEffect, useState, useCallback } from "react";
import { Link } from "react-router-dom";
import { apiClient } from "@/lib/axiosClient";
import PATHS from "@/routes/paths";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Users,
  UserCheck,
  UserX,
  Activity,
  UserPlus,
  RefreshCw,
  ArrowRight,
  ShieldCheck,
  AlertCircle
} from "lucide-react";

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [refreshing, setRefreshing] = useState(false);

  const fetchStats = useCallback(async (isManual = false) => {
    if (isManual) setRefreshing(true);
    else setLoading(true);
    setError(null);

    try {
      const response = await apiClient.get("/admin/stats");
      setStats(response.data);
    } catch (err) {
      console.error("Failed to load admin stats:", err);
      setError(err.response?.data?.message || "Failed to load dashboard statistics.");
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    fetchStats();
  }, [fetchStats]);

  return (
    <div className="space-y-8">
      {/* Page Header */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Admin Dashboard</h1>
          <p className="mt-1 text-sm text-muted-foreground">
            System overview, authentication metrics, and active user security posture.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={() => fetchStats(true)}
            disabled={loading || refreshing}
            className="flex items-center gap-1.5"
          >
            <RefreshCw className={`h-4 w-4 ${refreshing ? "animate-spin" : ""}`} />
            Refresh
          </Button>
          <Button size="sm" asChild>
            <Link to={PATHS.ADMIN.USER} className="flex items-center gap-1.5">
              Manage Users
              <ArrowRight className="h-4 w-4" />
            </Link>
          </Button>
        </div>
      </div>

      {/* Error Banner */}
      {error && (
        <div className="flex items-center justify-between rounded-lg border border-destructive/50 bg-destructive/10 p-4 text-destructive">
          <div className="flex items-center gap-3">
            <AlertCircle className="h-5 w-5 shrink-0" />
            <p className="text-sm font-medium">{error}</p>
          </div>
          <Button variant="outline" size="sm" onClick={() => fetchStats()}>
            Retry
          </Button>
        </div>
      )}

      {/* Stats Cards Grid */}
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5">
        {loading ? (
          Array.from({ length: 5 }).map((_, i) => (
            <Card key={i}>
              <CardHeader className="flex flex-row items-center justify-between pb-2">
                <Skeleton className="h-4 w-24" />
                <Skeleton className="h-5 w-5 rounded-full" />
              </CardHeader>
              <CardContent>
                <Skeleton className="h-8 w-16 mb-1" />
                <Skeleton className="h-3 w-32" />
              </CardContent>
            </Card>
          ))
        ) : (
          <>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between pb-2">
                <CardTitle className="text-sm font-medium text-muted-foreground">
                  Total Users
                </CardTitle>
                <Users className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{stats?.totalUsers ?? 0}</div>
                <p className="text-xs text-muted-foreground">Registered user accounts</p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex flex-row items-center justify-between pb-2">
                <CardTitle className="text-sm font-medium text-muted-foreground">
                  Verified Accounts
                </CardTitle>
                <UserCheck className="h-4 w-4 text-emerald-500" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{stats?.verifiedUsers ?? 0}</div>
                <p className="text-xs text-muted-foreground">
                  {stats?.totalUsers ? Math.round(((stats.verifiedUsers || 0) / stats.totalUsers) * 100) : 0}% verification rate
                </p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex flex-row items-center justify-between pb-2">
                <CardTitle className="text-sm font-medium text-muted-foreground">
                  Locked Accounts
                </CardTitle>
                <UserX className={`h-4 w-4 ${stats?.lockedAccounts > 0 ? "text-amber-500" : "text-muted-foreground"}`} />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{stats?.lockedAccounts ?? 0}</div>
                <p className="text-xs text-muted-foreground">Lockout or admin suspended</p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex flex-row items-center justify-between pb-2">
                <CardTitle className="text-sm font-medium text-muted-foreground">
                  Active Sessions
                </CardTitle>
                <Activity className="h-4 w-4 text-primary" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{stats?.activeSessions ?? 0}</div>
                <p className="text-xs text-muted-foreground">Valid rotating refresh tokens</p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="flex flex-row items-center justify-between pb-2">
                <CardTitle className="text-sm font-medium text-muted-foreground">
                  New Signups (24h)
                </CardTitle>
                <UserPlus className="h-4 w-4 text-blue-500" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{stats?.recentRegistrations ?? 0}</div>
                <p className="text-xs text-muted-foreground">Joined in last 24 hours</p>
              </CardContent>
            </Card>
          </>
        )}
      </div>

      {/* Quick Access & System Status */}
      <div className="grid gap-6 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">IAM Administration</CardTitle>
            <CardDescription>Quick links and common administrative actions</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex items-center justify-between p-3 border rounded-lg bg-card/50">
              <div className="flex items-center gap-3">
                <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary/10 text-primary">
                  <Users className="h-5 w-5" />
                </div>
                <div>
                  <h4 className="text-sm font-medium">User Directory</h4>
                  <p className="text-xs text-muted-foreground">Browse, search, lock/unlock, and change user roles</p>
                </div>
              </div>
              <Button variant="outline" size="sm" asChild>
                <Link to={PATHS.ADMIN.USER}>Manage</Link>
              </Button>
            </div>

            <div className="flex items-center justify-between p-3 border rounded-lg bg-card/50">
              <div className="flex items-center gap-3">
                <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-emerald-500/10 text-emerald-500">
                  <ShieldCheck className="h-5 w-5" />
                </div>
                <div>
                  <h4 className="text-sm font-medium">Security Profile</h4>
                  <p className="text-xs text-muted-foreground">Manage personal administrator account settings</p>
                </div>
              </div>
              <Button variant="outline" size="sm" asChild>
                <Link to={PATHS.USER.SECURITY}>Settings</Link>
              </Button>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Security & Token Policy</CardTitle>
            <CardDescription>Active safeguards in the authentication system</CardDescription>
          </CardHeader>
          <CardContent className="space-y-3">
            <div className="flex items-start gap-3 text-sm">
              <ShieldCheck className="h-4 w-4 text-emerald-500 mt-0.5 shrink-0" />
              <div>
                <span className="font-medium">Rotating Database Refresh Tokens:</span> Automatic reuse detection invalidates compromised token families.
              </div>
            </div>
            <div className="flex items-start gap-3 text-sm">
              <ShieldCheck className="h-4 w-4 text-emerald-500 mt-0.5 shrink-0" />
              <div>
                <span className="font-medium">Failed Login Lockout:</span> Accounts automatically lock after 5 consecutive failed attempts to protect against brute-force attacks.
              </div>
            </div>
            <div className="flex items-start gap-3 text-sm">
              <ShieldCheck className="h-4 w-4 text-emerald-500 mt-0.5 shrink-0" />
              <div>
                <span className="font-medium">Secure HttpOnly Cookie Transport:</span> Mitigates cross-site scripting (XSS) token exfiltration.
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}