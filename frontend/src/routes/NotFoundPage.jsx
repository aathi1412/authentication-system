import {Link} from "react-router-dom";

import {Button} from "../components/ui/button";

export default function NotFoundPage() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-3 bg-background px-4 text-center">
      <p className="text-5xl font-semibold tracking-tight text-foreground">404</p>
      <p className="text-sm text-muted-foreground">This page doesn't exist.</p>
      <Button asChild>
        <Link to="/account">Back to your account</Link>
      </Button>
    </div>
  );
}
