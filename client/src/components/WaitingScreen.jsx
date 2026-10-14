export default function WaitingScreen({ title, text }) {
  return (
    <main className="waiting">
      <div className="waiting-card">
        <div className="waiting-icon" aria-hidden="true">
          ♞
        </div>
        <h1>{title}</h1>
        {text && <p>{text}</p>}
      </div>
    </main>
  );
}
