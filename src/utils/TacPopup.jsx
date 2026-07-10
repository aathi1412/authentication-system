import {useState} from "react";
import TermsAndConditions from "./TermsAndConditions.jsx";

export function TacPopup(){
    const [open, setOpen] = useState(false);
    return(
        <>
            <label className="flex items-center gap-2 mb-5 text-sm">
                <input type="checkbox" required />
                I agree to
                <button
                    type="button"
                    onClick={() => setOpen(!open)}
                    className="text-blue-600 hover:underline"
                >
                    Read our Terms and Conditions
                </button>
            </label>

            {open && (
            <div className="fixed inset-0 bg-black/50 flex items-center justify-center">
                <div className="bg-white w-125 rounded-lg p-6">
                    <TermsAndConditions/>

                    <button
                        type="button"
                        onClick={() => setOpen(!open)}
                        className="ml-5 mt-4 bg-blue-600 text-white px-4 py-2 rounded-lg"
                    >close
                    </button>
                </div>
            </div>
            )}
        </>
    )
}

export default TacPopup