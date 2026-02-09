import {createSignal, createResource, For } from "solid-js";
import {adminApi} from "../api";


export default function Dashboard() {
    const [stats] = createResource(async () => {
        const response = await adminApi.getDashboard();
        return response.data.data;
    });

    const statCards = () => [
        { label: 'Total Users', value: stats()?.totalUsers || 0, icon: '👥', color: 'bg-blue-500' },
        { label: 'Active Users', value: stats()?.activeUsers || 0, icon: '✅', color: 'bg-green-500' },
        { label: 'Total Games', value: stats()?.totalGames || 0, icon: '🎮', color: 'bg-purple-500' },
        { label: 'Total Saves', value: stats()?.totalSaves || 0, icon: '💾', color: 'bg-orange-500' },
    ];

    return (
        <div>
            <h1 class="text-2xl font-bold text-gray-900 mb-6">Dashboard</h1>

            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                <For each={statCards()}>
                    {(stat) => (
                        <div class="card p-6">
                            <div class="flex items-center justify-between">
                                <div>
                                    <p class="text-sm text-gray-600">{stat.label}</p>
                                    <p class="text-3xl font-bold text-gray-900 mt-1">
                                        {stats.loading ? '...' : stat.value}
                                    </p>
                                </div>
                                <div class={`w-12 h-12 ${stat.color} rounded-lg flex items-center justify-center text-2xl`}>
                                    {stat.icon}
                                </div>
                            </div>
                        </div>
                    )}
                </For>
            </div>

            <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <div class="card p-6">
                    <h2 class="text-lg font-semibold mb-4">Quick Actions</h2>
                    <div class="space-y-3">
                        <a href="/users" class="block p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
                            <div class="flex items-center gap-3">
                                <span class="text-2xl">👥</span>
                                <div>
                                    <p class="font-medium">Manage Users</p>
                                    <p class="text-sm text-gray-600">View and edit user accounts</p>
                                </div>
                            </div>
                        </a>
                        <a href="/games" class="block p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
                            <div class="flex items-center gap-3">
                                <span class="text-2xl">🎮</span>
                                <div>
                                    <p class="font-medium">Manage Games</p>
                                    <p class="text-sm text-gray-600">Add or edit supported games</p>
                                </div>
                            </div>
                        </a>
                    </div>
                </div>

                <div class="card p-6">
                    <h2 class="text-lg font-semibold mb-4">System Status</h2>
                    <div class="space-y-4">
                        <div class="flex items-center justify-between">
                            <span class="text-gray-600">API Server</span>
                            <span class="px-2 py-1 bg-green-100 text-green-700 rounded-full text-sm">Online</span>
                        </div>
                        <div class="flex items-center justify-between">
                            <span class="text-gray-600">Database</span>
                            <span class="px-2 py-1 bg-green-100 text-green-700 rounded-full text-sm">Connected</span>
                        </div>
                        <div class="flex items-center justify-between">
                            <span class="text-gray-600">File Storage</span>
                            <span class="px-2 py-1 bg-green-100 text-green-700 rounded-full text-sm">Available</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
