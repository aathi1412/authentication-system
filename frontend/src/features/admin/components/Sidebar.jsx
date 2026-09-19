import BrandMark from '@/components/BrandMark';
import {Button} from "@/components/ui/button";
import {Sheet, SheetContent, SheetHeader, SheetTitle, SheetTrigger} from "@/components/ui/sheet";
import {cn} from "@/lib/utils";
import {Activity, LayoutDashboard, LogOut, Menu, Settings, Users} from "lucide-react";
import {useState} from "react";
import {NavLink, useNavigate} from "react-router-dom";
// const NAV_ITEMS = [
//     { to: PATHS.ADMIN.HOME, label: "dashboard", icon: LayoutGrid, end: true },
//     { to: PATHS.ADMIN.ACTIVITY, label: "activity", icon: UserRound },
//     { to: PATHS.ADMIN.SETTINGS, label: "settings", icon: ShieldCheck },
//     { to: PATHS.ADMIN.USER, label: "users", icon: ScrollText },
// ];

const NAV_ITEMS_TEMP = [
    { to: "1", label: "dashboard", icon: LayoutDashboard, end: true },
    { to: "2", label: "activity", icon: Activity },
    { to: "3", label: "settings", icon: Settings },
    { to: "#", label: "users", icon: Users },
];

function NavList({ onNavigate }){
    return(
        <nav>
            {NAV_ITEMS_TEMP.map(({to, label, icon: Icon, end}) => {
                console.log(icon);
                return(
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
                        <Icon className="h-4 w-4" />
                        {label}
                    </NavLink>
                )
            })}
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


function Sidebar() {
    const [mobileOpen, setMobileOpen] = useState(false);

    return (
        <>
            <aside className="hidden w-64 shrink-0 flex-col border-r border-border bg-background py-5 lg:fixed lg:inset-y-0 lg:flex">
                <BrandMark />
                <div className="mt-6 flex flex-1 flex-col justify-between">
                    <NavList />
                    <div className="px-2">
                        <LogoutButton />
                    </div>
                </div>
            </aside>

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
    )
}

export default Sidebar;