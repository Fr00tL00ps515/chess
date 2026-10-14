import Board from "./Board.jsx";

const NO_MOVES = [];

function statusText(game, color, opponentPresent) {
  const myTurn = game.turn === color;
  if (game.status === "CHECKMATE") return myTurn ? "Checkmate, you lost" : "Checkmate, you won!";
  if (game.status === "STALEMATE") return "Stalemate, it's a draw";
  if (!opponentPresent) return "Waiting for an opponent...";
  if (!myTurn) return "Opponent's move";
  return game.status === "CHECK" ? "Check! Your move" : "Your move";
}

export default function Game({ game, color, error, onMove, onRestart }) {
  const opponentPresent = game.players.includes(color === "WHITE" ? "BLACK" : "WHITE");
  const gameOver = game.status === "CHECKMATE" || game.status === "STALEMATE";
  const canMove = game.turn === color && opponentPresent && !gameOver;
  const inCheck = game.status === "CHECK" || game.status === "CHECKMATE";

  return (
    <main className="game">
      <header className="game-header">
        <span className={`color-badge ${color.toLowerCase()}`}>You play {color.toLowerCase()}</span>
        <h1 className="status">{statusText(game, color, opponentPresent)}</h1>
      </header>
      <Board
        board={game.board}
        color={color}
        legalMoves={canMove ? game.legalMoves : NO_MOVES}
        lastMove={game.lastMove}
        checkedKing={inCheck ? (game.turn === "WHITE" ? "K" : "k") : null}
        onMove={onMove}
      />
      <footer className="game-footer">
        {gameOver && (
          <button type="button" className="button" onClick={onRestart}>
            Play again
          </button>
        )}
        {error && <p className="error">{error}</p>}
      </footer>
    </main>
  );
}
