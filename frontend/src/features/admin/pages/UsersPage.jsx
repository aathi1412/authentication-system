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
        <div>
            <h1>Users</h1>

            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Status</th>
                    <th>Last Login</th>
                </tr>
                </thead>

                <tbody>
                {users.map((user) => (
                    <tr key={user.id}>
                        <td>{user.id}</td>
                        <td>{user.name}</td>

                        <td>{user.email}</td>
                        <td>{user.role}</td>
                        <td>
                            {user.accountLocked
                                ? "Locked"
                                : user.enabled
                                    ? "Active"
                                    : "Disabled"}
                        </td>
                        <td>
                            {user.lastLogin
                                ? new Date(user.lastLogin).toLocaleString()
                                : "Never"}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default UsersPage;