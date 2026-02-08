import { A } from "@solidjs/router";
import type { ParentProps } from "solid-js";
import { For } from "solid-js";
import { useAuth } from "../auth";

export default function Layout(props: ParentProps) {
    const auth = useAuth();

    const navItems = [
        { path: "/", label: "Dashboard", icon: "📊" },
        { path: "/users", label: "Users", icon: "👥" },
        { path: "/games", label: "Games", icon: "🎮" }
    ];

    return (
        <div class="min-h-screen bg-gray-100">
            <aside class="fixed inset-y-0 left-0 w-64 bg-gray-900 text-white">
                <div class="p-6">
                    <h1 class="text-xl font-bold flex items-center gap-2">
                        <span>🎮</span>
                        GameSave Admin
                    </h1>
                </div>

                <nav class="mt-6">
                    <For each={navItems}>
                        {(item) => (
                            <A
                                href={item.path}
                                class="flex items-center gap-3 px-6 py-3 text-gray-300 hover:bg-gray-800 hover:text-white transition-colors"
                                activeClass="bg-gray-800 text-white border-r-4 border-primary-500"
                                end={item.path === "/"}
                            >
                                <span>{item.icon}</span>
                                {item.label}
                            </A>
                        )}
                    </For>
                </nav>

                <div class="absolute bottom-0 left-0 right-0 p-4 border-t border-gray-800">
                    <div class="flex items-center gap-3 mb-3">
                        <div class="w-10 h-10 rounded-full bg-primary-600 flex items-center justify-center">
                            {auth.user()?.username?.[0]?.toUpperCase() || "?"}
                        </div>
                        <div>
                            <p class="font-medium">{auth.user()?.username}</p>
                            <p class="text-xs text-gray-400">{auth.user()?.role}</p>
                        </div>
                    </div>
                    <button
                        type="button"
                        onClick={() => auth.logout()}
                        class="w-full px-4 py-2 text-sm bg-gray-800 hover:bg-gray-700 rounded-lg transition-colors"
                    >
                        Sign Out
                    </button>
                </div>
            </aside>

            <main class="ml-64 p-8">
                {props.children}
            </main>
        </div>
    );
}
