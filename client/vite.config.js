import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/chess": {
        target: "http://localhost:8080",
        ws: true,
      },
    },
  },
});
