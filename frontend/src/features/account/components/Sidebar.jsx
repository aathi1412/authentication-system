import {Button} from "@/components/ui/button";
import {Sheet, SheetContent, SheetHeader, SheetTitle, SheetTrigger,} from "@/components/ui/sheet";
import {toast} from "@/components/ui/use-toast";
import {tokenStorage} from "@/lib/axiosClient";

import {cn} from "@/lib/utils";
import {KeyRound, LayoutGrid, LogOut, Menu, ScrollText, ShieldCheck, UserRound,} from "lucide-react";
import {useState} from "react";
import {NavLink, useNavigate} from "react-router-dom";
import PATHS from '../../../routes/paths'

const NAV_ITEMS = [
  { to: PATHS.USER.HOME, label: "Overview", icon: LayoutGrid, end: true },
  { to: PATHS.USER.PROFILE, label: "Profile", icon: UserRound },
  { to: PATHS.USER.SECURITY, label: "Security", icon: ShieldCheck },
  { to: PATHS.USER.ACTIVITY, label: "Activity Logs", icon: ScrollText },
];

function BrandMark() {
  return (
    <div className="flex items-center gap-2 px-2">
      <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary text-primary-foreground">
        <KeyRound className="h-4 w-4" />
      </div>
      <span className="text-sm font-semibold tracking-tight">SecureAuth</span>
    </div>
  );
}

function NavList({ onNavigate }) {
  return (
    <nav className="flex flex-1 flex-col gap-1 px-2">
      {NAV_ITEMS.map(({ to, label, icon: Icon, end }) => (
        <NavLink
          key={to}
          to={to}
          end={end}
          onClick={onNavigate}
          className={({ isActive }) =>
            cn(
              "flex items-center gap-2.5 rounded-md px-3 py-2 text-sm font-medium transition-colors",
              isActive
                ? "bg-secondary text-secondary-foreground"
                : "text-muted-foreground hover:bg-secondary/60 hover:text-foreground"
            )
          }
        >
          <Icon className="h-4 w-4 shrink-0" />
          {label}
        </NavLink>
      ))}
    </nav>
  );
}

function LogoutButton({ onNavigate }) {
  const navigate = useNavigate();

  const handleLogout = () => {
    tokenStorage.clear();
    toast({ title: "Signed out", description: "You've been logged out safely." });
    onNavigate?.();
    navigate(PATHS.AUTH.LOGIN);
  };

  return (
    <Button
      variant="ghost"
      onClick={handleLogout}
      className="justify-start gap-2.5 px-3 text-muted-foreground hover:text-destructive"
    >
      <LogOut className="h-4 w-4" />
      Logout
    </Button>
  );
}

/** Fixed sidebar on desktop; collapses into a Sheet drawer below the lg breakpoint. */
export function Sidebar() {
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <>
      {/* Desktop: fixed column */}
      <aside className="hidden w-64 shrink-0 flex-col border-r border-border bg-background py-5 lg:fixed lg:inset-y-0 lg:flex">
        <BrandMark />
        <div className="mt-6 flex flex-1 flex-col justify-between">
          <NavList />
          <div className="px-2">
            <LogoutButton />
          </div>
        </div>
      </aside>

      {/* Mobile: top bar with drawer trigger */}
      <header className="flex items-center justify-between border-b border-border bg-background px-4 py-3 lg:hidden">
        <BrandMark />
        <Sheet open={mobileOpen} onOpenChange={setMobileOpen}>
          <SheetTrigger asChild>
            <Button variant="outline" size="icon" aria-label="Open menu">
              <Menu className="h-4 w-4" />
            </Button>
          </SheetTrigger>
          <SheetContent side="left" className="flex w-72 flex-col p-0">
            <SheetHeader>
              <SheetTitle asChild>
                <BrandMark />
              </SheetTitle>
            </SheetHeader>
            <div className="flex flex-1 flex-col justify-between py-4">
              <NavList onNavigate={() => setMobileOpen(false)} />
              <div className="px-2">
                <LogoutButton onNavigate={() => setMobileOpen(false)} />
              </div>
            </div>
          </SheetContent>
        </Sheet>
      </header>
    </>
  );
}
