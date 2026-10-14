import { Client } from "@stomp/stompjs";
import { useCallback, useEffect, useRef, useState } from "react";

const BROKER_URL = `${window.location.protocol === "https:" ? "wss" : "ws"}://${window.location.host}/chess`;

export function useChessGame() {
  const clientRef = useRef(null);
  const [connected, setConnected] = useState(false);
  const [player, setPlayer] = useState(null);
  const [game, setGame] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    const client = new Client({
      brokerURL: BROKER_URL,
      reconnectDelay: 3000,
      onConnect: () => {
        client.subscribe("/user/output/player", (message) => setPlayer(JSON.parse(message.body)));
        client.subscribe("/user/output/errors", (message) => setError(JSON.parse(message.body).message));
        client.subscribe("/output/game", (message) => setGame(JSON.parse(message.body)));
        client.publish({ destination: "/input/join_game" });
        setConnected(true);
      },
      onWebSocketClose: () => {
        setConnected(false);
        setPlayer(null);
        setGame(null);
      },
    });
    client.activate();
    clientRef.current = client;
    return () => client.deactivate();
  }, []);

  useEffect(() => {
    if (!error) return;
    const timeout = setTimeout(() => setError(null), 3000);
    return () => clearTimeout(timeout);
  }, [error]);

  const sendMove = useCallback((move) => {
    clientRef.current?.publish({ destination: "/input/move", body: JSON.stringify(move) });
  }, []);

  const restart = useCallback(() => {
    clientRef.current?.publish({ destination: "/input/restart" });
  }, []);

  return { connected, player, game, error, sendMove, restart };
}
