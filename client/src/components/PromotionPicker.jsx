import { pieceGlyph, pieceName } from "../pieces.js";

const CHOICES = ["Q", "R", "B", "N"];

export default function PromotionPicker({ color, onPick, onCancel }) {
  return (
    <div className="promotion" onClick={onCancel}>
      <div className="promotion-choices" onClick={(event) => event.stopPropagation()}>
        {CHOICES.map((letter) => {
          const piece = color === "WHITE" ? letter : letter.toLowerCase();
          return (
            <button key={letter} type="button" aria-label={pieceName(piece)} onClick={() => onPick(letter)}>
              <span className={`piece ${color.toLowerCase()}`}>{pieceGlyph(piece)}</span>
            </button>
          );
        })}
      </div>
    </div>
  );
}
