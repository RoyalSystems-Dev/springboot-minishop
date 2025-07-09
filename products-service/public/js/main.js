import { createApp, h } from 'vue';
import App from "./App.js";

const app = createApp({
    render: () => h(App)
});

app.mount('#app');