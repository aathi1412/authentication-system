import PATHS from '../../../routes/paths'

export default function Home(){

    const { name, email } = JSON.parse(localStorage.getItem("userResponse")) || {};
    
    return (
        <>
            <div>
                welcome to the home page.
                {name}
            </div>
            <div>
                <a
                href={PATHS.AUTH.LOGIN}
                >
                    login
                </a>
            </div>
        </>
    )
}