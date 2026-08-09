import {
  LogIn,
  KeyRound,
  UserRoundPen,
  MailCheck,
  ShieldAlert,
  RotateCcw,
  Activity,
} from "lucide-react";

import { Badge } from "@/components/ui/badge";

const TYPE_META = {
  LOGIN: { icon: LogIn, tone: "secondary" },
  PASSWORD_CHANGE: { icon: KeyRound, tone: "success" },
  PROFILE_UPDATE: { icon: UserRoundPen, tone: "secondary" },
  EMAIL_VERIFIED: { icon: MailCheck, tone: "success" },
  FAILED_LOGIN: { icon: ShieldAlert, tone: "destructive" },
  PASSWORD_RESET: { icon: RotateCcw, tone: "warning" },
};

const CATEGORY_BADGE_VARIANT = {
  Security: "success",
  Profile: "secondary",
  Authentication: "warning",
};

function formatDate(iso) {
  const date = new Date(iso);
  return {
    date: date.toLocaleDateString(undefined, {
      month: "short",
      day: "numeric",
      year: "numeric",
    }),
    time: date.toLocaleTimeString(undefined, {
      hour: "numeric",
      minute: "2-digit",
    }),
  };
}

function ActivityItem({ item, isLast }) {
  const meta = TYPE_META[item.type] || { icon: Activity, tone: "secondary" };
  const Icon = meta.icon;
  const { date, time } = formatDate(item.createdAt);

  const iconToneClass = {
    success: "bg-success/10 text-success",
    destructive: "bg-destructive/10 text-destructive",
    warning: "bg-warning/10 text-warning",
    secondary: "bg-muted text-muted-foreground",
  }[meta.tone];

  return (
    <li className="relative flex gap-4 pb-6 last:pb-0">
      {!isLast && (
        <span className="absolute left-[19px] top-10 h-[calc(100%-2.25rem)] w-px bg-border" />
      )}
      <div
        className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-full ${iconToneClass}`}
      >
        <Icon className="h-4 w-4" />
      </div>

      <div className="flex flex-1 flex-col gap-1 pt-1 sm:flex-row sm:items-start sm:justify-between">
        <div className="space-y-0.5">
          <div className="flex flex-wrap items-center gap-2">
            <p className="text-sm font-medium text-foreground">{item.title}</p>
            {item.category && (
              <Badge variant={CATEGORY_BADGE_VARIANT[item.category] || "secondary"}>
                {item.category}
              </Badge>
            )}
          </div>
          <p className="text-sm text-muted-foreground">{item.description}</p>
        </div>
        <div className="shrink-0 whitespace-nowrap text-xs text-muted-foreground sm:text-right">
          <div>{date}</div>
          <div>{time}</div>
        </div>
      </div>
    </li>
  );
}

export function ActivityTimeline({ items }) {
  return (
    <ul>
      {items.map((item, index) => (
        <ActivityItem key={item.id} item={item} isLast={index === items.length - 1} />
      ))}
    </ul>
  );
}
