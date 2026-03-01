import {createSignal, createResource, For, Show, JSX} from "solid-js";
import { adminApi } from "../api";

type Game = {
    id: string;
    name: string;
    slug: string;
    description?: string | null;
    iconUrl?: string | null;
    isActive: boolean;
    saveCount?: number | null;
};

type Page<T> = {
    content: T[];
};

type ApiResponse<T> = {
    data: T;
};

export default function Games() {
    const [showModal, setShowModal] = createSignal(false);
    const [editGame, setEditGame] = createSignal<Game | null>(null);

    const [games, { refetch }] = createResource<Page<Game>>(async () => {
        const response = await adminApi.getGames(0, 100) as ApiResponse<{ data: Page<Game> }>;
        return response.data.data;
    });

    const handleCreate = () => {
        setEditGame(null);
        setShowModal(true);
    };

    const handleEdit = (game: Game) => {
        setEditGame(game);
        setShowModal(true);
    };

    const handleSave: JSX.EventHandlerUnion<HTMLFormElement, SubmitEvent> = async (e) => {
        e.preventDefault();

        const form = e.currentTarget;
        const formData = new FormData(form);

        const name = String(formData.get("name") ?? "");
        const slug = String(formData.get("slug") ?? "");
        const description = String(formData.get("description") ?? "");
        const iconUrl = String(formData.get("iconUrl") ?? "");

        const payload: Omit<Game, "id"> = {
            name,
            slug,
            description: description || null,
            iconUrl: iconUrl || null,
            isActive: true,
            saveCount: null
        };

        const current = editGame();
        if (current) {
            payload.isActive = String(formData.get("isActive")) === "true";
            await adminApi.updateGame(current.id, payload);
        } else {
            await adminApi.createGame(payload);
        }

        setShowModal(false);
        refetch();
    };

    const handleDelete = async (game: Game) => {
        if (confirm(`Delete game ${game.name}?`)) {
            await adminApi.deleteGame(game.id);
            refetch();
        }
    };

    const generateSlug = (name: string) =>
        name
            .toLowerCase()
            .replace(/[^a-z0-9]+/g, "-")
            .replace(/^-+|-+$/g, "");

    return (
        <div>
            <div class="flex items-center justify-between mb-6">
                <h1 class="text-2xl font-bold text-gray-900">Games</h1>
                <button type="button" onClick={handleCreate} class="btn btn-primary">
                    + Add Game
                </button>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <Show when={!games.loading} fallback={<p class="text-gray-500">Loading...</p>}>
                    <For each={games()?.content ?? []}>
                        {(game) => (
                            <div class="card p-6">
                                <div class="flex items-start justify-between mb-4">
                                    <div class="flex items-center gap-3">
                                        <div class="w-12 h-12 bg-gray-100 rounded-lg flex items-center justify-center text-2xl">
                                            <Show
                                                when={game.iconUrl}
                                                fallback={"🎮"}
                                            >
                                                <img
                                                    src={game.iconUrl!}
                                                    alt={game.name}
                                                    class="w-full h-full object-cover rounded-lg"
                                                />
                                            </Show>
                                        </div>
                                        <div>
                                            <h3 class="font-semibold text-gray-900">{game.name}</h3>
                                            <p class="text-sm text-gray-500">{game.slug}</p>
                                        </div>
                                    </div>

                                    <span
                                        class={`px-2 py-1 rounded-full text-xs font-medium ${
                                            game.isActive
                                                ? "bg-green-100 text-green-700"
                                                : "bg-red-100 text-red-700"
                                        }`}
                                    >
                    {game.isActive ? "Active" : "Inactive"}
                  </span>
                                </div>

                                <p class="text-sm text-gray-600 mb-4 line-clamp-2">
                                    {game.description || "No description"}
                                </p>

                                <div class="flex items-center justify-between text-sm">
                                    <span class="text-gray-500">{game.saveCount ?? 0} saves</span>
                                    <div class="flex gap-2">
                                        <button
                                            type="button"
                                            onClick={() => handleEdit(game)}
                                            class="text-primary-600 hover:text-primary-800"
                                        >
                                            Edit
                                        </button>
                                        <button
                                            type="button"
                                            onClick={() => handleDelete(game)}
                                            class="text-red-600 hover:text-red-800"
                                        >
                                            Delete
                                        </button>
                                    </div>
                                </div>
                            </div>
                        )}
                    </For>
                </Show>
            </div>

            <Show when={showModal()}>
                <div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                    <div class="card p-6 w-full max-w-md">
                        <h2 class="text-xl font-bold mb-4">
                            {editGame() ? "Edit Game" : "Add Game"}
                        </h2>

                        <form onSubmit={handleSave} class="space-y-4">
                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">Name</label>
                                <input
                                    type="text"
                                    name="name"
                                    class="input"
                                    required
                                    value={editGame()?.name ?? ""}
                                    onInput={(e) => {
                                        if (!editGame()) {
                                            const form = e.currentTarget.form;
                                            if (!form) return;
                                            const slugInput = form.querySelector<HTMLInputElement>('[name="slug"]');
                                            if (slugInput) slugInput.value = generateSlug(e.currentTarget.value);
                                        }
                                    }}
                                />
                            </div>

                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">Slug</label>
                                <input
                                    type="text"
                                    name="slug"
                                    class="input"
                                    required
                                    value={editGame()?.slug ?? ""}
                                    disabled={!!editGame()}
                                    pattern="^[a-z0-9-]+$"
                                />
                                <p class="text-xs text-gray-500 mt-1">
                                    Lowercase letters, numbers, and hyphens only
                                </p>
                            </div>

                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">Description</label>
                                <textarea
                                    name="description"
                                    class="input"
                                    rows={3}
                                    value={editGame()?.description ?? ""}
                                />
                            </div>

                            <div>
                                <label class="block text-sm font-medium text-gray-700 mb-1">Icon URL</label>
                                <input
                                    type="url"
                                    name="iconUrl"
                                    class="input"
                                    value={editGame()?.iconUrl ?? ""}
                                    placeholder="https://..."
                                />
                            </div>

                            <Show when={editGame()}>
                                <div>
                                    <label class="block text-sm font-medium text-gray-700 mb-1">Status</label>
                                    <select name="isActive" class="input" value={String(editGame()!.isActive)}>
                                        <option value="true">Active</option>
                                        <option value="false">Inactive</option>
                                    </select>
                                </div>
                            </Show>

                            <div class="flex gap-2 pt-4">
                                <button type="submit" class="btn btn-primary flex-1">Save</button>
                                <button
                                    type="button"
                                    onClick={() => setShowModal(false)}
                                    class="btn btn-secondary flex-1"
                                >
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
