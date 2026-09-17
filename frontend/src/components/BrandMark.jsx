import {KeyRound} from "lucide-react";

function BrandMark() {
    return (
        <div className="flex items-center gap-2 px-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary text-primary-foreground">
                <KeyRound className="h-4 w-4" />
            </div>
            <span className="text-sm font-semibold tracking-tight">AuthService</span>
        </div>
    );
}

export default BrandMark;