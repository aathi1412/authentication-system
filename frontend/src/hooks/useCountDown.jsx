import {useEffect, useState} from "react";

export default function useCountDown(initialSeconds = 60) {

    const [seconds, setSeconds] = useState(initialSeconds);

    useEffect(() => {
        if (seconds <= 0) return;

        const timer = setInterval(() => {
            setSeconds((prev) => prev - 1);
        }, 1000)

        return () => {
            clearInterval(timer);
        }
    }, [seconds]);

    const start = () => {
        setSeconds(initialSeconds);
    };

    return {
        start,
        seconds,
        isRunning: seconds > 0
    };
}