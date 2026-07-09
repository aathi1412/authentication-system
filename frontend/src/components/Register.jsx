import EmailPasswordButton from "./EmailPasswordButton.jsx";

export function Register(){
    return(
        <>
            <div>
                <input type="text" placeholder="Enter your Name"/>
                <EmailPasswordButton/>
            </div>
        </>
    )
}

export default Register;