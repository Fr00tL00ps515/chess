import { useState } from "react";
import PromotionPicker from "./PromotionPicker.jsx";
import { isWhite, pieceGlyph, pieceName } from "../pieces.js";

const FILES = "abcdefgh";
const WHITE_VIEW = [0, 1, 2, 3, 4, 5, 6, 7];
const BLACK_VIEW = [7, 6, 5, 4, 3, 2, 1, 0];

export default function Board({ board, color, legalMoves, lastMove, checkedKing, onMove }) {
  const [selected, setSelected] = useState(null);
  const [promotion, setPromotion] = useState(null);

  const view = color === "WHITE" ? WHITE_VIEW : BLACK_VIEW;
  const movesFrom = (row, col) => legalMoves.filter((move) => move.fromRow === row && move.fromCol === col);
  const targets = selected ? movesFrom(selected.row, selected.col) : [];
  const active = targets.length > 0 ? selected : null;

  function handleClick(row, col) {
    const move = targets.find((target) => target.toRow === row && target.toCol === col);
    const reselect = active?.row === row && active?.col === col;
    setSelected(null);
    if (move) {
      const isPawn = board[move.fromRow][move.fromCol].toLowerCase() === "p";
      if (isPawn && (row === 0 || row === 7)) {
        setPromotion(move);
      } else {
        onMove(move);
      }
    } else if (!reselect && movesFrom(row, col).length > 0) {
      setSelected({ row, col });
    }
  }

  function isHighlighted(row, col) {
    if (active?.row === row && active?.col === col) return true;
    if (!lastMove) return false;
    return (
      (lastMove.fromRow === row && lastMove.fromCol === col) || (lastMove.toRow === row && lastMove.toCol === col)
    );
  }

  return (
    <div className="board">
      {view.map((row) =>
        view.map((col) => {
          const piece = board[row][col];
          const isTarget = targets.some((target) => target.toRow === row && target.toCol === col);
          const classes = [
            "square",
            (row + col) % 2 === 0 ? "light" : "dark",
            isHighlighted(row, col) && "highlight",
            isTarget && (piece ? "capture" : "target"),
            piece && piece === checkedKing && "check",
          ];
          return (
            <button
              key={`${row}-${col}`}
              type="button"
              className={classes.filter(Boolean).join(" ")}
              aria-label={`${FILES[col]}${8 - row}${piece ? `, ${pieceName(piece)}` : ""}`}
              onClick={() => handleClick(row, col)}
            >
              {col === view[0] && <span className="coordinate rank">{8 - row}</span>}
              {row === view[7] && <span className="coordinate file">{FILES[col]}</span>}
              {piece && <span className={`piece ${isWhite(piece) ? "white" : "black"}`}>{pieceGlyph(piece)}</span>}
            </button>
          );
        }),
      )}
      {promotion && (
        <PromotionPicker
          color={color}
          onPick={(letter) => {
            onMove({ ...promotion, promotion: letter });
            setPromotion(null);
          }}
          onCancel={() => setPromotion(null)}
        />
      )}
    </div>
  );
}
