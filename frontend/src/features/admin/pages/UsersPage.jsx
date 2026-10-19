import { useState, useEffect, useCallback } from "react";
import { apiClient, tokenStorage } from "@/lib/axiosClient";
import { useDebounce } from "@/hooks/useDebounce";
import { toast } from "@/components/ui/use-toast";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import {
  Search,
  MoreVertical,
  Lock,
  Unlock,
  Shield,
  LogOut,
  UserX,
  RefreshCw,
  ChevronLeft,
  ChevronRight,
  AlertCircle,
  Users as UsersIcon,
} from "lucide-react";

export default function UsersPage() {
  const currentUser = tokenStorage.getUserResponse();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Pagination & Filters
  const [page, setPage] = useState(0);
  const pageSize = 10;
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [search, setSearch] = useState("");
  const debouncedSearch = useDebounce(search, 350);
  const [roleFilter, setRoleFilter] = useState("ALL");
  const [statusFilter, setStatusFilter] = useState("ALL");

  // Mutation states
  const [actionLoadingId, setActionLoadingId] = useState(null);
  const [roleModalUser, setRoleModalUser] = useState(null);
  const [selectedRole, setSelectedRole] = useState("USER");
  const [deleteModalUser, setDeleteModalUser] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const fetchUsers = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const params = {
        page,
        pageSize,
      };

      if (debouncedSearch && debouncedSearch.trim()) {
        params.search = debouncedSearch.trim();
      }

      if (roleFilter !== "ALL") {
        params.role = roleFilter;
      }

      if (statusFilter === "LOCKED") {
        params.accountLocked = true;
      } else if (statusFilter === "ACTIVE") {
        params.enabled = true;
        params.accountLocked = false;
      } else if (statusFilter === "DISABLED") {
        params.enabled = false;
      }

      const response = await apiClient.get("/admin/users", { params });
      const data = response.data;
      setUsers(data.content || []);
      setTotalPages(data.totalPages || 0);
      setTotalElements(data.totalElements || 0);
    } catch (err) {
      console.error("Failed to fetch users:", err);
      setError(err.response?.data?.message || "Failed to load users list.");
    } finally {
      setLoading(false);
    }
  }, [page, pageSize, debouncedSearch, roleFilter, statusFilter]);

  useEffect(() => {
    let ignore = false;
    const loadData = async () => {
      setLoading(true);
      setError(null);
      try {
        const params = { page, pageSize };
        if (debouncedSearch && debouncedSearch.trim()) {
          params.search = debouncedSearch.trim();
        }
        if (roleFilter !== "ALL") {
          params.role = roleFilter;
        }
        if (statusFilter === "LOCKED") {
          params.accountLocked = true;
        } else if (statusFilter === "ACTIVE") {
          params.enabled = true;
          params.accountLocked = false;
        } else if (statusFilter === "DISABLED") {
          params.enabled = false;
        }

        const response = await apiClient.get("/admin/users", { params });
        if (!ignore) {
          const data = response.data;
          setUsers(data.content || []);
          setTotalPages(data.totalPages || 0);
          setTotalElements(data.totalElements || 0);
        }
      } catch (err) {
        if (!ignore) {
          console.error("Failed to fetch users:", err);
          setError(err.response?.data?.message || "Failed to load users list.");
        }
      } finally {
        if (!ignore) {
          setLoading(false);
        }
      }
    };

    loadData();
    return () => {
      ignore = true;
    };
  }, [page, pageSize, debouncedSearch, roleFilter, statusFilter]);

  // Handle Lock / Unlock
  const handleToggleLock = async (user) => {
    if (user.id === currentUser?.id) {
      toast({
        variant: "destructive",
        title: "Action Forbidden",
        description: "You cannot lock your own account.",
      });
      return;
    }

    setActionLoadingId(user.id);
    const endpoint = user.accountLocked ? "unlock" : "lock";
    try {
      await apiClient.put(`/admin/users/${user.id}/${endpoint}`);
      toast({
        title: user.accountLocked ? "Account Unlocked" : "Account Locked",
        description: `Successfully ${user.accountLocked ? "unlocked" : "locked"} ${user.name}'s account.`,
      });
      fetchUsers();
    } catch (err) {
      toast({
        variant: "destructive",
        title: "Operation Failed",
        description: err.response?.data?.message || `Failed to ${endpoint} user.`,
      });
    } finally {
      setActionLoadingId(null);
    }
  };

  // Handle Session Revocation
  const handleRevokeSessions = async (user) => {
    setActionLoadingId(user.id);
    try {
      await apiClient.post(`/admin/users/${user.id}/revoke-sessions`);
      toast({
        title: "Sessions Revoked",
        description: `All active refresh sessions for ${user.name} have been terminated.`,
      });
    } catch (err) {
      toast({
        variant: "destructive",
        title: "Revocation Failed",
        description: err.response?.data?.message || "Failed to revoke sessions.",
      });
    } finally {
      setActionLoadingId(null);
    }
  };

  // Handle Role Change
  const handleRoleChangeSubmit = async () => {
    if (!roleModalUser) return;
    setIsSubmitting(true);
    try {
      await apiClient.put(`/admin/users/${roleModalUser.id}/role`, {
        role: selectedRole,
      });
      toast({
        title: "Role Updated",
        description: `${roleModalUser.name}'s role was changed to ${selectedRole}.`,
      });
      setRoleModalUser(null);
      fetchUsers();
    } catch (err) {
      toast({
        variant: "destructive",
        title: "Role Update Failed",
        description: err.response?.data?.message || "Failed to update role.",
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  // Handle Disable / Soft Delete
  const handleDeleteSubmit = async () => {
    if (!deleteModalUser) return;
    setIsSubmitting(true);
    try {
      await apiClient.delete(`/admin/users/${deleteModalUser.id}`);
      toast({
        title: "Account Disabled",
        description: `${deleteModalUser.name}'s account has been disabled and sessions revoked.`,
      });
      setDeleteModalUser(null);
      fetchUsers();
    } catch (err) {
      toast({
        variant: "destructive",
        title: "Action Failed",
        description: err.response?.data?.message || "Failed to disable account.",
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  const getRoleBadge = (role) => {
    switch (role) {
      case "SUPER_ADMIN":
        return <Badge variant="default" className="bg-purple-600 hover:bg-purple-700">Super Admin</Badge>;
      case "ADMIN":
        return <Badge variant="default" className="bg-blue-600 hover:bg-blue-700">Admin</Badge>;
      default:
        return <Badge variant="secondary">User</Badge>;
    }
  };

  const getStatusBadge = (user) => {
    if (user.accountLocked) {
      return (
        <Badge variant="destructive" className="flex items-center gap-1">
          <Lock className="h-3 w-3" />
          Locked
        </Badge>
      );
    }
    if (!user.enabled) {
      return (
        <Badge variant="warning" className="flex items-center gap-1">
          <UserX className="h-3 w-3" />
          Disabled
        </Badge>
      );
    }
    return (
      <Badge variant="success" className="flex items-center gap-1">
        <span className="h-1.5 w-1.5 rounded-full bg-emerald-500" />
        Active
      </Badge>
    );
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">User Management</h1>
          <p className="mt-1 text-sm text-muted-foreground">
            Manage system identities, assign roles, inspect account status, and control active sessions.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={fetchUsers}
            disabled={loading}
            className="flex items-center gap-1.5"
          >
            <RefreshCw className={`h-4 w-4 ${loading ? "animate-spin" : ""}`} />
            Refresh
          </Button>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <Card>
        <CardContent className="p-4">
          <div className="flex flex-col gap-3 md:flex-row md:items-center">
            {/* Search */}
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                placeholder="Search by name or email..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="pl-9"
              />
            </div>

            {/* Role Filter */}
            <Select value={roleFilter} onValueChange={setRoleFilter}>
              <SelectTrigger className="w-full md:w-44">
                <SelectValue placeholder="Filter by Role" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ALL">All Roles</SelectItem>
                <SelectItem value="SUPER_ADMIN">Super Admin</SelectItem>
                <SelectItem value="ADMIN">Admin</SelectItem>
                <SelectItem value="USER">User</SelectItem>
              </SelectContent>
            </Select>

            {/* Status Filter */}
            <Select value={statusFilter} onValueChange={setStatusFilter}>
              <SelectTrigger className="w-full md:w-44">
                <SelectValue placeholder="Filter by Status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ALL">All Statuses</SelectItem>
                <SelectItem value="ACTIVE">Active</SelectItem>
                <SelectItem value="LOCKED">Locked</SelectItem>
                <SelectItem value="DISABLED">Disabled</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </CardContent>
      </Card>

      {/* Error Banner */}
      {error && (
        <div className="flex items-center justify-between rounded-lg border border-destructive/50 bg-destructive/10 p-4 text-destructive">
          <div className="flex items-center gap-3">
            <AlertCircle className="h-5 w-5 shrink-0" />
            <p className="text-sm font-medium">{error}</p>
          </div>
          <Button variant="outline" size="sm" onClick={fetchUsers}>
            Retry
          </Button>
        </div>
      )}

      {/* Users Table */}
      <div className="rounded-lg border bg-card shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="border-b bg-muted/40 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
              <tr>
                <th className="px-6 py-3.5">User</th>
                <th className="px-6 py-3.5">Role</th>
                <th className="px-6 py-3.5">Status</th>
                <th className="px-6 py-3.5">Created</th>
                <th className="px-6 py-3.5">Last Login</th>
                <th className="px-6 py-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {loading ? (
                Array.from({ length: 5 }).map((_, i) => (
                  <tr key={i}>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        <Skeleton className="h-9 w-9 rounded-full" />
                        <div className="space-y-1.5">
                          <Skeleton className="h-4 w-32" />
                          <Skeleton className="h-3 w-44" />
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4"><Skeleton className="h-5 w-16" /></td>
                    <td className="px-6 py-4"><Skeleton className="h-5 w-16" /></td>
                    <td className="px-6 py-4"><Skeleton className="h-4 w-24" /></td>
                    <td className="px-6 py-4"><Skeleton className="h-4 w-24" /></td>
                    <td className="px-6 py-4 text-right"><Skeleton className="h-8 w-8 ml-auto rounded" /></td>
                  </tr>
                ))
              ) : users.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center text-muted-foreground">
                    <div className="flex flex-col items-center justify-center gap-2">
                      <UsersIcon className="h-8 w-8 text-muted-foreground/60" />
                      <p className="font-medium">No users found</p>
                      <p className="text-xs">Try adjusting your search query or filters.</p>
                    </div>
                  </td>
                </tr>
              ) : (
                users.map((user) => {
                  const isCurrent = user.id === currentUser?.id;
                  const isActionLoading = actionLoadingId === user.id;

                  return (
                    <tr key={user.id} className="hover:bg-muted/30 transition-colors">
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-3">
                          <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-primary/10 text-primary font-semibold text-sm">
                            {user.name ? user.name.charAt(0).toUpperCase() : "U"}
                          </div>
                          <div className="min-w-0">
                            <div className="flex items-center gap-1.5">
                              <span className="font-medium text-foreground truncate">{user.name}</span>
                              {isCurrent && (
                                <span className="rounded bg-primary/10 px-1.5 py-0.2 text-[10px] font-semibold text-primary">
                                  You
                                </span>
                              )}
                            </div>
                            <p className="text-xs text-muted-foreground truncate">{user.email}</p>
                          </div>
                        </div>
                      </td>

                      <td className="px-6 py-4 whitespace-nowrap">
                        {getRoleBadge(user.role)}
                      </td>

                      <td className="px-6 py-4 whitespace-nowrap">
                        {getStatusBadge(user)}
                      </td>

                      <td className="px-6 py-4 whitespace-nowrap text-xs text-muted-foreground">
                        {user.createdAt ? new Date(user.createdAt).toLocaleDateString() : "—"}
                      </td>

                      <td className="px-6 py-4 whitespace-nowrap text-xs text-muted-foreground">
                        {user.lastLogin ? new Date(user.lastLogin).toLocaleString() : "Never"}
                      </td>

                      <td className="px-6 py-4 whitespace-nowrap text-right">
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <Button
                              variant="ghost"
                              size="icon"
                              className="h-8 w-8"
                              disabled={isActionLoading}
                            >
                              <MoreVertical className="h-4 w-4" />
                            </Button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end" className="w-48">
                            <DropdownMenuLabel>Actions</DropdownMenuLabel>
                            <DropdownMenuSeparator />

                            {/* Lock / Unlock */}
                            <DropdownMenuItem
                              onClick={() => handleToggleLock(user)}
                              disabled={isCurrent}
                              className="cursor-pointer"
                            >
                              {user.accountLocked ? (
                                <>
                                  <Unlock className="mr-2 h-4 w-4 text-emerald-500" />
                                  <span>Unlock Account</span>
                                </>
                              ) : (
                                <>
                                  <Lock className="mr-2 h-4 w-4 text-amber-500" />
                                  <span>Lock Account</span>
                                </>
                              )}
                            </DropdownMenuItem>

                            {/* Change Role */}
                            <DropdownMenuItem
                              onClick={() => {
                                setRoleModalUser(user);
                                setSelectedRole(user.role || "USER");
                              }}
                              disabled={isCurrent}
                              className="cursor-pointer"
                            >
                              <Shield className="mr-2 h-4 w-4 text-primary" />
                              <span>Change Role</span>
                            </DropdownMenuItem>

                            {/* Revoke Sessions */}
                            <DropdownMenuItem
                              onClick={() => handleRevokeSessions(user)}
                              className="cursor-pointer"
                            >
                              <LogOut className="mr-2 h-4 w-4 text-blue-500" />
                              <span>Revoke Sessions</span>
                            </DropdownMenuItem>

                            <DropdownMenuSeparator />

                            {/* Disable Account */}
                            <DropdownMenuItem
                              onClick={() => setDeleteModalUser(user)}
                              disabled={isCurrent || !user.enabled}
                              className="cursor-pointer text-destructive focus:text-destructive"
                            >
                              <UserX className="mr-2 h-4 w-4" />
                              <span>Disable Account</span>
                            </DropdownMenuItem>
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination Bar */}
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between border-t px-6 py-4 bg-card">
          <p className="text-xs text-muted-foreground">
            Showing {users.length > 0 ? page * pageSize + 1 : 0} to{" "}
            {Math.min((page + 1) * pageSize, totalElements)} of {totalElements} users
          </p>

          <div className="flex items-center gap-2">
            <span className="text-xs text-muted-foreground mr-2">
              Page {totalPages > 0 ? page + 1 : 1} of {totalPages || 1}
            </span>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
              disabled={page === 0 || loading}
            >
              <ChevronLeft className="h-4 w-4 mr-1" />
              Previous
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setPage((prev) => prev + 1)}
              disabled={page >= totalPages - 1 || loading || totalPages === 0}
            >
              Next
              <ChevronRight className="h-4 w-4 ml-1" />
            </Button>
          </div>
        </div>
      </div>

      {/* Change Role Dialog */}
      <Dialog open={!!roleModalUser} onOpenChange={(open) => !open && setRoleModalUser(null)}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Change User Role</DialogTitle>
            <DialogDescription>
              Modify access permissions for <strong>{roleModalUser?.name}</strong> ({roleModalUser?.email}).
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-4 py-2">
            <label className="text-sm font-medium">Select Role</label>
            <Select value={selectedRole} onValueChange={setSelectedRole}>
              <SelectTrigger>
                <SelectValue placeholder="Select a role" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="USER">User (Standard user access)</SelectItem>
                <SelectItem value="ADMIN">Admin (User management & stats)</SelectItem>
                <SelectItem value="SUPER_ADMIN">Super Admin (Full system control)</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <DialogFooter className="gap-2 sm:gap-0">
            <Button
              variant="outline"
              onClick={() => setRoleModalUser(null)}
              disabled={isSubmitting}
            >
              Cancel
            </Button>
            <Button onClick={handleRoleChangeSubmit} disabled={isSubmitting}>
              {isSubmitting ? "Saving..." : "Save Role"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Disable Account Dialog */}
      <Dialog open={!!deleteModalUser} onOpenChange={(open) => !open && setDeleteModalUser(null)}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Disable Account</DialogTitle>
            <DialogDescription>
              Are you sure you want to disable <strong>{deleteModalUser?.name}</strong>?
              This will immediately revoke all active refresh tokens and prevent the user from logging in.
            </DialogDescription>
          </DialogHeader>

          <DialogFooter className="gap-2 sm:gap-0">
            <Button
              variant="outline"
              onClick={() => setDeleteModalUser(null)}
              disabled={isSubmitting}
            >
              Cancel
            </Button>
            <Button
              variant="destructive"
              onClick={handleDeleteSubmit}
              disabled={isSubmitting}
            >
              {isSubmitting ? "Disabling..." : "Disable Account"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}