import {Router, Route, Navigate, useNavigate} from "@solidjs/router";
import {Show, type ParentProps, createEffect} from "solid-js";
import { AuthProvider, useAuth } from "./auth";

import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Users from "./pages/Users";
import Games from "./pages/Games";
import Layout from "./components/Layout";

function ProtectedRoot(props: ParentProps) {
    const auth: any = useAuth();

    return (
        <Show
            when={auth.isAuthenticated() && auth.isAdmin()}
            fallback={<Navigate href="/login" />}
        >
            <Layout>{props.children}</Layout>
        </Show>
    );
}

function ProtectedLayout(props: ParentProps) {
    const auth: any = useAuth();
    const navigate = useNavigate();

    createEffect(() => {
        if (!auth.loading() && (!auth.isAuthenticated() || !auth.isAdmin())) {
            navigate('/login', { replace: true });
        }
    });

    return (
        <Show when={!auth.loading() && auth.isAuthenticated() && auth.isAdmin()}>
            <Layout>{props.children}</Layout>
        </Show>
    );;
}

export default function App() {
    return (
        <AuthProvider>
            <Router>
                <Route path={"/login"} component={Login} />
                <Route path={"/"} component={ProtectedLayout}>
                    <Route path="/" component={Dashboard} />
                    <Route path="/users" component={Users} />
                    <Route path="/games" component={Games} />
                </Route>
            </Router>
        </AuthProvider>
    );
}
