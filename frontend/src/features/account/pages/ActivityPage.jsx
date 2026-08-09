import { useState } from "react";
import { Search, ScrollText } from "lucide-react";

import { useActivityLogs } from "@/features/account/hooks/useActivityLogs";
import { useDebounce } from "@/hooks/useDebounce";
import { useInfiniteScroll } from "@/hooks/useInfiniteScroll";
import { ActivityTimeline } from "@/features/account/components/ActivityTimeline";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import { Spinner } from "@/components/Spinner";
import { EmptyState } from "@/components/EmptyState";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

const CATEGORY_OPTIONS = [
  { value: "ALL", label: "All" },
  { value: "Security", label: "Security" },
  { value: "Profile", label: "Profile" },
  { value: "Authentication", label: "Authentication" },
];

function TimelineSkeleton() {
  return (
    <div className="space-y-6">
      {Array.from({ length: 5 }).map((_, i) => (
        <div key={i} className="flex gap-4">
          <Skeleton className="h-10 w-10 shrink-0 rounded-full" />
          <div className="flex-1 space-y-2 pt-1">
            <Skeleton className="h-4 w-40" />
            <Skeleton className="h-3.5 w-64" />
          </div>
        </div>
      ))}
    </div>
  );
}

export default function ActivityPage() {
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("ALL");
  const debouncedSearch = useDebounce(search, 350);

  const { items, isLoading, isFetchingMore, hasMore, loadMore } = useActivityLogs({
    search: debouncedSearch,
    category,
  });

  const sentinelRef = useInfiniteScroll({
    onIntersect: loadMore,
    hasMore,
    isLoading: isLoading || isFetchingMore,
  });

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold tracking-tight">Activity logs</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          A history of logins, changes, and other account activity.
        </p>
      </div>

      <div className="flex flex-col gap-3 sm:flex-row">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search activity..."
            className="pl-9"
          />
        </div>
        <Select value={category} onValueChange={setCategory}>
          <SelectTrigger className="sm:w-48">
            <SelectValue placeholder="Filter" />
          </SelectTrigger>
          <SelectContent>
            {CATEGORY_OPTIONS.map((opt) => (
              <SelectItem key={opt.value} value={opt.value}>
                {opt.label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Recent activity</CardTitle>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <TimelineSkeleton />
          ) : items.length === 0 ? (
            <EmptyState
              icon={ScrollText}
              title="No activity found"
              description="Try adjusting your search or filter to find what you're looking for."
            />
          ) : (
            <>
              <ActivityTimeline items={items} />
              {hasMore && (
                <div ref={sentinelRef} className="flex justify-center pt-4">
                  {isFetchingMore && <Spinner className="text-muted-foreground" />}
                </div>
              )}
            </>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
