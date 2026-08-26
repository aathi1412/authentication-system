import {useState} from "react";
import TacPopup from "../components/TacPopup.jsx";

export function TermsAndConditions({register, errors}) {
    const [open, setOpen] = useState(false);
    return (
        <div>
            <label className="flex items-start gap-3 cursor-pointer">
                <input
                    type="checkbox"
                    {...register("acceptedTerms")}
                    className="
                         mt-1 h-4 w-4 rounded
                         accent-gray-500
                        border-gray-300
                        text-gray-600
                    "
                />
                <span>
                    I agree to{" "}
                    <button
                        type="button"
                        onClick={() => setOpen(!open)}
                        className="
                            font-medium
                            text-black-900
                            underline
                            hover:text-gray-700
                            hover:underline-offset-4
                        "
                    >
                        Read our Terms and Conditions
                    </button>
                </span>
            </label>

            {errors.acceptedTerms && (
                <p className="mt-2 text-sm text-red-600 mb-5">
                    {errors.acceptedTerms.message}
                </p>
            )}
            {open && (
                <div className="fixed inset-0 z-50 bg-black/60 flex items-center justify-center p-4">
                    <div className="max-h-[70vh] bg-white overflow-y-auto px-6 py-5">
                        <TacPopup/>

                        <div className="flex justify-end gap-3 px-6 py-4">
                            <button
                                type="button"
                                onClick={() => setOpen(false)}
                                className="
                                rounded-lg
                                bg-blue-600
                                px-5
                                py-2
                                font-medium
                                text-white
                                hover:bg-blue-700
                            "
                            >
                                Close
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}

export default TermsAndConditions