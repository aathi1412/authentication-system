import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080/api/auth",
    timeout: 5000,
    withCredentials: true
})

export default api