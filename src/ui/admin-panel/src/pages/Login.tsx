import { createSignal } from 'solid-js';
import { useNavigate } from '@solidjs/router';
import { useAuth } from '../auth';

export default function Login () {
    const [username, setUsername] = createSignal('');
    const [password, setPassword] = createSignal('');
    const [error, setError] = createSignal('');
    const auth: any = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e: any) => {
        e.preventDefault();
        e.stopPropagation();
        setError('')

        const result = await auth.login(username(), password());

        console.info(result.success);

        if (result.success) {
            if (auth.isAdmin()) {
                navigate('/', {replace: true});
            } else {
                setError("Admin access required");
                auth.logout();
            }
        } else {
            setError(result.error);
        }
    };

    return (
        <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 to-gray-800">
            <div class="w-full max-w-md">
                <div class="card p-8">
                    <div class="text-center mb-8">
                        <h1 class="text-3xl font-bold text-gray-900 flex items-center justify-center gap-2">
                            <span>🎮</span>
                            GameSave
                        </h1>
                        <p class="text-gray-600 mt-2">Admin Panel</p>
                    </div>

                    {error() && (
                        <div class="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-lg text-sm">
                            {error()}
                        </div>
                    )}

                    <form onSubmit={(e) => e.preventDefault()} class="space-y-4">
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">
                                Username
                            </label>
                            <input
                                type="text"
                                class="input"
                                value={username()}
                                onInput={(e) => setUsername(e.target.value)}
                                placeholder="Enter username"
                                required
                            />
                        </div>

                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">
                                Password
                            </label>
                            <input
                                type="password"
                                class="input"
                                value={password()}
                                onInput={(e) => setPassword(e.target.value)}
                                placeholder="Enter password"
                                required
                            />
                        </div>

                        <button
                            type="button"
                            class="btn btn-primary w-full"
                            disabled={auth.loading()}
                            onClick={handleSubmit}
                        >
                            {auth.loading() ? 'Signing in...' : 'Sign in'}
                        </button>
                    </form>

                    <p class="text-center text-sm text-gray-500 mt-6">
                        Default: admin / admin123
                    </p>
                </div>
            </div>
        </div>
    );
};

