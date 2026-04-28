import { create_main_page } from "./main_page.js";

export function create_login_page(document: Document, restIp: String): void {
  const elements = {
    title: document.createElement("h1"),
    form: document.createElement("div"),
    userInput: document.createElement("input"),
    userLabel: document.createElement("p"),
    passInput: document.createElement("input"),
    passLabel: document.createElement("p"),
    submitBtn: document.createElement("button"),
    regBtn: document.createElement("button"),
  };

  const container = document.getElementById("div");

  if (container) {
    // 1. Setup Content
    elements.title.innerText = "Sign In";
    elements.userLabel.innerText = "User Name";
    elements.passLabel.innerText = "Password";
    elements.submitBtn.innerText = "Login";
    elements.regBtn.innerText = "Register";

    // 2. Setup Input Types
    elements.userInput.placeholder = "Enter Username";
    elements.passInput.type = "password"; // This hides the password!
    elements.passInput.placeholder = "Enter Password";

    // 3. Nesting (The "Assembly Line")
    // Add everything to the form
    elements.form.appendChild(elements.userLabel);
    elements.form.appendChild(elements.userInput);
    elements.form.appendChild(elements.passLabel);
    elements.form.appendChild(elements.passInput);
    elements.form.appendChild(elements.submitBtn);
    elements.form.appendChild(elements.regBtn);

    // Add the title and the complete form to the page
    container.appendChild(elements.title);
    container.appendChild(elements.form);

    elements.submitBtn.addEventListener("click", () =>
      sign_in(container, elements, restIp),
    );
    elements.regBtn.addEventListener("click", () => register(elements, restIp));
  }
}

async function sign_in(container: any, elements: any, restIp: String) {
  let message = {
    userName: elements.userInput.value,
    password: elements.passInput.value,
  };

  let response: any = await (
    await fetch(restIp + "" + "/sign_in", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(message),
    })
  ).json();

  if (!response.inSystem) {
    console.log("Something went wrong");
  } else {
    create_main_page(document, container, response);
  }
}

async function register(elements: any, restIp: String) {
  let message = {
    userName: elements.userInput.value,
    password: elements.passInput.value,
  };

  let response: any = await (
    await fetch(restIp + "" + "/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(message),
    })
  ).json();

  let registered = document.createElement("p");
  elements.form.appendChild(registered);
  registered.innerHTML = "REGISTERED";
}
