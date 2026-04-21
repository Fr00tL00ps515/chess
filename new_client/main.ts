import { create_login_page } from "./modules/login_page.js";

const restIp = "http://localhost:8080";
// const ws = new WebSocket("ws://localhost:9090/chess");

function main(): void {
  create_login_page(document, restIp);
}

main();
