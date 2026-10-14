const GLYPHS = { k: "♚", q: "♛", r: "♜", b: "♝", n: "♞", p: "♟" };
const NAMES = { k: "king", q: "queen", r: "rook", b: "bishop", n: "knight", p: "pawn" };

export function isWhite(piece) {
  return piece === piece.toUpperCase();
}

export function pieceGlyph(piece) {
  return GLYPHS[piece.toLowerCase()] + "︎";
}

export function pieceName(piece) {
  return `${isWhite(piece) ? "white" : "black"} ${NAMES[piece.toLowerCase()]}`;
}
