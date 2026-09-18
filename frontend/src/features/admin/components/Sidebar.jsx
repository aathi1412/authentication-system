import BrandMark from '@/components/BrandMark';
import {cn} from "@/lib/utils";
import {LayoutGrid, ScrollText, ShieldCheck, UserRound} from "lucide-react";
import {NavLink} from "react-router-dom";

// const NAV_ITEMS = [
//     { to: PATHS.ADMIN.HOME, label: "dashboard", icon: LayoutGrid, end: true },
//     { to: PATHS.ADMIN.ACTIVITY, label: "activity", icon: UserRound },
//     { to: PATHS.ADMIN.SETTINGS, label: "settings", icon: ShieldCheck },
//     { to: PATHS.ADMIN.USER, label: "users", icon: ScrollText },
// ];

const NAV_ITEMS_TEMP = [
    { to: "1", label: "dashboard", icon: LayoutGrid, end: true },
    { to: "2", label: "activity", icon: UserRound },
    { to: "3", label: "settings", icon: ShieldCheck },
    { to: "#", label: "users", icon: ScrollText },
];

function NavList({ onNavigate }){
    return(
        <nav>
            {NAV_ITEMS_TEMP.map(({to, label, icon, end}) => {
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
                        {/*<Icon className="h-4 w-4 shrink-0" />*/}
                        {label}
                    </NavLink>
                )
            })}
        </nav>
    );
}

function Sidebar() {
    return (
        <>
            <aside className="hidden w-64 shrink-0 flex-col border-r border-border bg-background py-5 lg:fixed lg:inset-y-0 lg:flex">
                <BrandMark />
                <div className="mt-6 flex flex-1 flex-col justify-between">
                    <NavList />
                    <div className="px-2">
                        {/*<LogoutButton />*/}
                    </div>
                </div>
            </aside>
        </>
    )
}

export default Sidebar;