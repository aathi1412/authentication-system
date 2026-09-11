import {useEffect, useState} from 'react';
import {apiClient} from '../../../lib/axiosClient'

function UsersPage() {
    const [loading, setLoading] = useState(true);
    const [users, setUsers] = useState();

    useEffect(() => {
        const fetchUsers = async () => {
            try{
                const response = await apiClient.get("/admin/users");
                console.log(response.data);
                setUsers(response.data.content)
            }
            catch (err) {
                console.log(err);
            }
            finally{
                setLoading(false);
            }
        }

        fetchUsers();
    }, []);

    return (
        <>
            <h1>Users</h1>
            {loading && <p>Loading User data....</p>}
            {users && users.map((user) => (
                <div key={user.id}>
                    <p>{user.name}</p>
                    <p>{user.email}</p>
                    <p>{user.role}</p>
                </div>
            ))}
        </>
    );
}

export default UsersPage;