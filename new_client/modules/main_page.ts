export function create_main_page(
  document: Document,
  container: HTMLElement,
  user: any,
) {
  // 1. Clear the container
  container.innerHTML = "";

  // 2. Create the Wrapper/Card for the Profile
  const profileCard = document.createElement("div");
  profileCard.style.cssText = `
        padding: 20px;
        border: 1px solid #444;
        border-radius: 8px;
        background-color: #262421;
        color: #fff;
        max-width: 400px;
        margin: 20px auto;
        font-family: sans-serif;
        text-align: center;
    `;

  // 3. User Name Heading
  const nameHeader = document.createElement("h2");
  nameHeader.textContent = user.userName || "Unknown Player";
  profileCard.appendChild(nameHeader);

  // 4. Stats Section
  const statsDiv = document.createElement("div");
  statsDiv.style.margin = "15px 0";
  statsDiv.innerHTML = `
        <p>Wins: <span style="color: #81b64c; font-weight: bold;">${user.wins || 0}</span></p>
        <p>Losses: <span style="color: #fa412d; font-weight: bold;">${user.losses || 0}</span></p>
    `;
  profileCard.appendChild(statsDiv);

  // 5. Search Opponent Button
  const searchBtn = document.createElement("button");
  searchBtn.textContent = "Search for Opponent";
  searchBtn.style.cssText = `
        background-color: #81b64c;
        color: white;
        border: none;
        padding: 10px 20px;
        font-size: 1.1rem;
        cursor: pointer;
        border-radius: 4px;
        font-weight: bold;
    `;

  searchBtn.onclick = () => {
    console.log("Searching for a match...");
    // Add your matchmaking logic here
  };

  profileCard.appendChild(searchBtn);

  // 6. Append everything to the main container
  container.appendChild(profileCard);
}
