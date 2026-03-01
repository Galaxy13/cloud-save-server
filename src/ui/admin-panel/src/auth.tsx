import {createSignal, createContext, useContext} from "solid-js";
import {authApi} from "./api";

const AuthContext = createContext();

export function AuthProvider(props:any) {
    const [user, setUser] = createSignal(JSON.parse(localStorage.getItem('user') as string));
    const [loading, setLoading] = createSignal(false)

    const login = async (username: String, password: String) => {
        setLoading(true);
        try {
            const response = await authApi.login(username, password);
            const { token, refreshToken, user: userData } = response.data.data;

            console.log({'accessToken': token, 'refreshToken': refreshToken, 'user': userData});

            localStorage.setItem('token', token);
            localStorage.setItem('refreshToken', refreshToken);
            localStorage.setItem('user', JSON.stringify(userData))

            setUser(userData);
            return {success: true};
        } catch (e: any) {
            return {
                success: false,
                error: e.response?.data?.error?.message || 'Login failed'
            };
        } finally {
            setLoading(false);
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
        setUser(null)
    };

    const isAdmin = () => user()?.role === 'ADMIN';

    const value = {
        user,
        loading,
        login,
        logout,
        isAdmin,
        isAuthenticated: () => !!user()
    };

    return (
        <AuthContext.Provider value={value}>
            {props.children}
        </AuthContext.Provider>
    )
}

export function useAuth() {
    return useContext(AuthContext);
}