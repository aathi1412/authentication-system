import { Loader2 } from "lucide-react";

import { cn } from "@/lib/utils";

/**
 * Small inline loading spinner. Pair with a label for buttons ("Saving…")
 * or drop in alone for inline loading states.
 */
export function Spinner({ className, size = 16 }) {
  return (
    <Loader2
      className={cn("animate-spin text-current", className)}
      style={{ width: size, height: size }}
      aria-hidden="true"
    />
  );
}
