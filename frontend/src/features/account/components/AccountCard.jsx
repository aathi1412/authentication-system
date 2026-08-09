import { Card, CardContent } from "@/components/ui/card";
import { cn } from "@/lib/utils";

/**
 * Compact "fact" card used across the Security page: an icon, a label, and
 * a value (or badge) on the right. Keeps the security info grid consistent
 * without repeating layout markup in every field.
 */
export function AccountCard({ icon: Icon, label, value, className }) {
  return (
    <Card className={cn("transition-colors hover:border-foreground/15", className)}>
      <CardContent className="flex items-center justify-between gap-4 p-4">
        <div className="flex items-center gap-3">
          {Icon && (
            <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-muted">
              <Icon className="h-4 w-4 text-muted-foreground" />
            </div>
          )}
          <p className="text-sm text-muted-foreground">{label}</p>
        </div>
        <div className="text-sm font-medium">{value}</div>
      </CardContent>
    </Card>
  );
}
