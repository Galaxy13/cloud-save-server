import {createSignal, createResource, For, Show} from "solid-js";
import { adminApi} from "../api";

export default function Users() {
    const [page, setPage] = createSignal(0);
    const [selectedUser, setSelectedUser] = createSignal(null);
    const [showModal, setShowModel] = createSignal(false);

    const [users, { refetch }] = createResource(page, async (p) => {
        const response = await adminApi.getUsers(p, 20);
        return response.data.data;
    });

    const formatBytes = (bytes) => {
        if (!bytes) return '0 B';
        const k = 1024;
        const sizes = ['B', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    };

    const formatDate = (date) => {
        if (!date) return 'Never';
        return new Date(date).toLocaleDateString();
    };

    const handleEdit = (user) => {
        setSelectedUser(user);
        setShowModal(true);
    };

    const handleSave = async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);

        await adminApi.updateUser(selectedUser().id, {
            email: formData.get('email'),
            role: formData.get('role'),
            isActive: formData.get('isActive') === 'true'
        });

        setShowModal(false);
        refetch();
    };

    const handleDelete = async (user) => {
        if (confirm(`Delete user ${user.username}?`)) {
            await adminApi.deleteUser(user.id);
            refetch();
        }
    };

    return (
        <div>
            <div class="flex items-center justify-between mb-6">
                <h1 class="text-2xl font-bold text-gray-900">Users</h1>
            </div>

            <div class="card overflow-hidden">
                <table class="w-full">
                    <thead>
                    <tr class="table-header">
                        <th class="px-6 py-3">User</th>
                        <th class="px-6 py-3">Role</th>
                        <th class="px-6 py-3">Saves</th>
                        <th class="px-6 py-3">Storage</th>
                        <th class="px-6 py-3">Status</th>
                        <th class="px-6 py-3">Last Login</th>
                        <th class="px-6 py-3">Actions</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-200">
                    <Show when={!users.loading} fallback={
                        <tr><td colspan="7" class="px-6 py-8 text-center text-gray-500">Loading...</td></tr>
                    }>
                        <For each={users()?.content || []}>
                            {(user) => (
                                <tr class="hover:bg-gray-50">
                                    <td class="px-6 py-4">
                                        <div>
                                            <p class="font-medium text-gray-900">{user.username}</p>
                                            <p class="text-sm text-gray-500">{user.email}</p>
                                        </div>
                                    </td>
                                    <td class="px-6 py-4">
                      <span class={`px-2 py-1 rounded-full text-xs font-medium ${
                          user.role === 'ADMIN' ? 'bg-purple-100 text-purple-700' : 'bg-gray-100 text-gray-700'
                      }`}>
                        {user.role}
                      </span>
                                    </td>
                                    <td class="px-6 py-4 text-gray-600">{user.totalSaves || 0}</td>
                                    <td class="px-6 py-4 text-gray-600">{formatBytes(user.totalStorage)}</td>
                                    <td class="px-6 py-4">
                      <span class={`px-2 py-1 rounded-full text-xs font-medium ${
                          user.isActive ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                      }`}>
                        {user.isActive ? 'Active' : 'Inactive'}
                      </span>
                                    </td>
                                    <td class="px-6 py-4 text-gray-600">{formatDate(user.lastLogin)}</td>
                                    <td class="px-6 py-4">
                                        <div class="flex gap-2">
                                            <button
                                                onClick={() => handleEdit(user)}
                                                class="text-primary-600 hover:text-primary-800"
                                            >
                                                Edit
                                            </button>
                                            <button
                                                onClick={() => handleDelete(user)}
                                                class="text-red-600 hover:text-red-800"
                                            >
                                                Delete
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            )}
                        </For>
                    </Show>
                    </tbody>
                </table>

                <Show when={users()}>
                    <div class="px-6 py-4 border-t border-gray-200 flex items-center justify-between">
                        <p class="text-sm text-gray-600">
                            Page {(users()?.page || 0) + 1} of {users()?.totalPages || 1}
                        </p>
                        <div class="flex gap-2">
                            <button
                                onClick={() => setPage(p => Math.max(0, p - 1))}
                                disabled={users()?.first}
                                class="btn btn-secondary text-sm disabled:opacity-50"
                            >
                                Previous
                            </button>
                            <button
                                onClick={() => setPage(p => p + 1)}
                                disabled={users()?.last}
                                class="btn btn-secondary text-sm disabled:opacity-50"
                            >
                                Next
                            </button>
                        </div>
                    </div>
                </Show>
            </div>

            {/* Edit Modal */}
            <Show when={showModal()}>
                <div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                    <div class="card p-6 w-full max-w-md">
                        <h2 class="text-xl font-bold mb-4">Edit User</h2>
                        <form onSubmit={handleSave} class="space-y-4">
                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
                                <input
                                    type="email"
                                    name="email"
                                    class="input"
                                    value={selectedUser()?.email || ''}
                                />
                            </div>
                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">Role</label>
                                <select name="role" class="input">
                                    <option value="USER" selected={selectedUser()?.role === 'USER'}>User</option>
                                    <option value="ADMIN" selected={selectedUser()?.role === 'ADMIN'}>Admin</option>
                                </select>
                            </div>
                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">Status</label>
                                <select name="isActive" class="input">
                                    <option value="true" selected={selectedUser()?.isActive}>Active</option>
                                    <option value="false" selected={!selectedUser()?.isActive}>Inactive</option>
                                </select>
                            </div>
                            <div class="flex gap-2 pt-4">
                                <button type="submit" class="btn btn-primary flex-1">Save</button>
                                <button type="button" onClick={() => setShowModal(false)} class="btn btn-secondary flex-1">
                                    Cancel
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </Show>
        </div>
    );
}