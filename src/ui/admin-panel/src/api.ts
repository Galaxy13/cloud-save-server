import axios from "axios";

const API_BASE = '/api/v1';

const api = axios.create({
    baseURL: API_BASE,
    headers: {
        'Content-Type': "application/json"
    }
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
})

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            const isAuthRequest = error.config?.url?.includes('/auth/');
            if (!isAuthRequest) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = '/login';
            }
        }
        return Promise.reject(error)
    }
);

export const authApi = {
    login: (username: String, password: String) =>
        api.post('/auth/login', {username, password}),

    register: (username: String, email: String, password: String) =>
        api.post("/auth/register", {username, email, password}),

    refresh: (refreshToken: String) =>
        api.post('/auth/refresh', {refreshToken})
};

export const adminApi = {
    getDashboard: () => api.get('/admin/dashboard'),

    getUsers: (page = 0, size = 20) =>
        api.get(`/admin/users?page=${page}&size=${size}`),

    getUser: (id: String) => api.get(`/admin/users/${id}`),

    updateUser: (id:String, data:any) => api.patch('/admin/users/${id}', data),

    deleteUser: (id: String) => api.delete(`/admin/users/${id}`),

    resetPassword: (id: String, newPassword: String) => api.post('/admin/users/${id}/reset-password', {newPassword}),

    getGames: (page = 0, size = 50) =>
        api.get(`/admin/games?page=${page}&size=${size}`),

    createGame: (data: any) => api.post('/admin/games', data),

    updateGame: (id: String, data: any) => api.patch(`/admin/games/${id}`, data),

    deleteGame: (id: String) => api.delete(`/admin/games/${id}`),
};

export const userApi = {
    getProfile: () => api.get('/users/me'),
};

export default api;