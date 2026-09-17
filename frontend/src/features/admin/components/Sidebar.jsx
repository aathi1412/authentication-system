import BrandMark from '@/components/BrandMark';
import {LayoutGrid, ScrollText, ShieldCheck, UserRound} from "lucide-react";

const NAV_ITEMS = [
    { to: PATHS.ADMIN.HOME, label: "dashboard", icon: LayoutGrid, end: true },
    { to: PATHS.ADMIN.ACTIVITY, label: "activity", icon: UserRound },
    { to: PATHS.ADMIN.SETTINGS, label: "settings", icon: ShieldCheck },
    { to: PATHS.ADMIN.USER, label: "users", icon: ScrollText },
];



function Sidebar() {
    return (
        <>
            <aside>
                <BrandMark />
            </aside>
        </>
    )
}

export default Sidebar;