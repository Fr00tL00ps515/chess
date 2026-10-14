import Game from "./components/Game.jsx";
import WaitingScreen from "./components/WaitingScreen.jsx";
import { useChessGame } from "./useChessGame.js";

function queueText(position) {
  const place = position === 1 ? "You are next in line." : `You are number ${position} in the line.`;
  return `${place} The board opens as soon as one of the players leaves.`;
}

export default function App() {
  const { connected, player, game, error, sendMove, restart } = useChessGame();

  if (!connected) {
    return <WaitingScreen title="Connecting to the server..." />;
  }
  if (player?.color && game) {
    return <Game game={game} color={player.color} error={error} onMove={sendMove} onRestart={restart} />;
  }
  if (player?.queuePosition) {
    return <WaitingScreen title="Both seats are taken" text={queueText(player.queuePosition)} />;
  }
  return <WaitingScreen title="Joining the game..." />;
}
