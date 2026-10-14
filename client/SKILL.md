## Hi AGENT!

name: chess-client
description: Architecture, file structure and design rules of the React chess client. Use when adding or changing anything inside client/.

---

# Chess Client

Thin React front end for a two-player online chess game. All game rules, move validation and state live on the Spring Boot server. The client only renders the state it receives and sends user intents back.

## Stack

- React 19 with function components and hooks
- Vite 8 for dev server and build
- `@stomp/stompjs` for STOMP over WebSocket
- Plain CSS with custom properties, no CSS framework
- oxlint for linting
- JavaScript (`.js` / `.jsx`), no TypeScript

## Commands

- `npm run dev` starts Vite and proxies `/chess` to `http://localhost:8080`
- `npm run build` produces `dist/`
- `npm run lint` runs oxlint

## File Structure

```
client/
├── index.html
├── vite.config.js
├── package.json
├── public/
│   └── favicon.svg
└── src/
    ├── main.jsx
    ├── App.jsx
    ├── useChessGame.js
    ├── pieces.js
    ├── index.css
    └── components/
        ├── Game.jsx
        ├── Board.jsx
        ├── PromotionPicker.jsx
        └── WaitingScreen.jsx
```

| File                             | Responsibility                                                        |
| -------------------------------- | --------------------------------------------------------------------- |
| `main.jsx`                       | Mounts `<App />` into `#root`                                         |
| `App.jsx`                        | Top-level screen router based on connection, player and game state    |
| `useChessGame.js`                | Single owner of the STOMP client, subscriptions and outgoing messages |
| `pieces.js`                      | Pure helpers for piece letters: color, glyph, accessible name         |
| `components/Game.jsx`            | Game screen layout, status text, derives what the board may show      |
| `components/Board.jsx`           | 8x8 grid, selection, move targets, highlights, promotion trigger      |
| `components/PromotionPicker.jsx` | Modal for choosing the promotion piece                                |
| `components/WaitingScreen.jsx`   | Generic centered card for connecting / joining / queue states         |
| `index.css`                      | All styles and design tokens                                          |

## Architecture

### Data flow

```
server ──STOMP──▶ useChessGame ──props──▶ App ──▶ Game ──▶ Board ──▶ PromotionPicker
   ▲                                                         │
   └──────────── sendMove / restart callbacks ◀──────────────┘
```

- `useChessGame` is the only place that talks to the network. Components never import `@stomp/stompjs`.
- State flows down as props, intents flow up as callbacks. There is no global store or context.
- The server is authoritative. The client never applies a move locally; it waits for the next `/output/game` message.

### STOMP protocol

Broker URL: `ws(s)://<host>/chess`

| Direction | Destination          | Payload                                                                |
| --------- | -------------------- | ---------------------------------------------------------------------- |
| subscribe | `/user/output/player` | `{ color: "WHITE" \| "BLACK" \| null, queuePosition: number \| null }` |
| subscribe | `/user/output/errors` | `{ message: string }`                                                  |
| subscribe | `/output/game`        | game state, see below                                                  |
| send      | `/input/join_game`          | none, sent on every connect                                            |
| send      | `/input/move`          | `{ fromRow, fromCol, toRow, toCol, promotion? }`                       |
| send      | `/input/restart`       | none                                                                   |

Game state:

```json
{
  "board": [["r","n","b","q","k","b","n","r"], ...],
  "turn": "WHITE",
  "status": "ONGOING | CHECK | CHECKMATE | STALEMATE",
  "players": ["WHITE", "BLACK"],
  "legalMoves": [{ "fromRow": 6, "fromCol": 4, "toRow": 4, "toCol": 4 }],
  "lastMove": { "fromRow": 6, "fromCol": 4, "toRow": 4, "toCol": 4 }
}
```

### Conventions

- Board is `board[row][col]`, row 0 is rank 8, col 0 is file a.
- Pieces are single FEN letters: uppercase is white, lowercase is black, empty square is `""` or `null`.
- Promotion is sent as an uppercase letter `Q`, `R`, `B` or `N`.
- Black's view is produced by reversing row and column iteration order, never by mutating the board.
- On socket close all game state is reset, and `reconnectDelay` handles reconnection.
- Errors auto-clear after 3 seconds.

### Screen selection in `App`

1. Not connected → `WaitingScreen` "Connecting to the server..."
2. Has a color and a game → `Game`
3. Has a queue position → `WaitingScreen` with queue text
4. Otherwise → `WaitingScreen` "Joining the game..."

## Code Style

- One default-exported component per file in `components/`, named like the file.
- Hooks and helpers use named exports.
- Derive values during render instead of storing them in state.
- Constants in `UPPER_SNAKE_CASE` at module top, e.g. `FILES`, `WHITE_VIEW`, `NO_MOVES`.
- Small pure helper functions next to the component that uses them, e.g. `statusText`, `queueText`.
- Class names built from an array and joined with `filter(Boolean).join(" ")`.
- Double quotes, semicolons, trailing commas, 2-space indent, line width around 120.
- No comments in code unless the logic is genuinely non-obvious.
- Every interactive square or choice is a `<button type="button">` with an `aria-label`.

## Design

Dark theme inspired by chess.com.

### Tokens

All colors are CSS custom properties on `:root` in `index.css`. Never hardcode a color in a rule.

| Token               | Value     | Use                           |
| ------------------- | --------- | ----------------------------- |
| `--background`      | `#302e2b` | Page background               |
| `--surface`         | `#262421` | Cards, panels                 |
| `--text`            | `#ffffff` | Primary text                  |
| `--text-muted`      | `#bababa` | Secondary text                |
| `--accent`          | `#81b64c` | Primary buttons               |
| `--accent-hover`    | `#95c95f` | Button hover                  |
| `--accent-shadow`   | `#45753c` | Button bottom shadow          |
| `--danger`          | `#fa412d` | Errors, check                 |
| `--light-square`    | `#ebecd0` | Light squares                 |
| `--dark-square`     | `#739552` | Dark squares                  |
| `--highlight-light` | `#f5f682` | Selected / last move on light |
| `--highlight-dark`  | `#b9ca43` | Selected / last move on dark  |

### Visual rules

- Font is the system UI stack.
- Pieces are Unicode glyphs from `pieces.js` with the text variation selector, colored via `.piece.white` / `.piece.black`.
- Square states: `.highlight` for selection and last move, `.target` dot for quiet moves, `.capture` ring for captures, `.check` red glow on the king.
- Coordinates are drawn inside the edge squares, rank on the left column, file on the bottom row of the current view.
- Cards use `12px` radius and a soft dark shadow.
- Layout is centered and must work down to 320px width; the board scales with `min()` of viewport width and height.
