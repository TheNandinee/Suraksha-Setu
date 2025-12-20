import React, { useEffect, useState } from "react";
import "./App.css";

export default function App() {
  const [logs, setLogs] = useState([]);

  // Mock one real entry, rest filler for UI
  useEffect(() => {
    setLogs([
      {
        id: "1",
        text: "You are stupid",
        blocked: true,
        toxicity: 0.92,
        ts: Date.now(),
      },
      {
        id: "2",
        text: "You are amazing!",
        blocked: false,
        toxicity: 0.05,
        ts: Date.now() - 60000,
      }
    ]);
  }, []);

  const latest = logs[0] || {};

  return (
    <div className="wrapper">

      {/* TITLE */}
      <h1 className="title">🔥 Aegis Moderation Dashboard</h1>

      {/* SUMMARY CARDS */}
      <div className="summary-grid">
        <div className="summary-card">
          <h2>{logs.length}</h2>
          <p>Total Messages</p>
        </div>

        <div className="summary-card">
          <h2>{logs.filter(l => l.blocked).length}</h2>
          <p>Blocked</p>
        </div>

        <div className="summary-card">
          <h2>{logs.filter(l => !l.blocked).length}</h2>
          <p>Allowed</p>
        </div>

        <div className="summary-card">
          <h2>
            {logs.length ? Math.round(logs.reduce((a,b) => a + b.toxicity, 0)/logs.length * 100) : 0}%
          </h2>
          <p>Avg Toxicity</p>
        </div>
      </div>

      {/* LATEST MESSAGE */}
      <div className="latest-card">
        <h2>Latest Message</h2>

        {latest.id ? (
          <>
            <p className="latest-text">“{latest.text}”</p>

            <div className="tox-bar">
              <div
                className="tox-bar-fill"
                style={{ width: `${latest.toxicity * 100}%`,
                         background: latest.blocked ? "#ff5252" : "#4caf50" }}
              />
            </div>

            <p className="tox-score">
              Toxicity: <b>{Math.round(latest.toxicity * 100)}%</b>
            </p>

            <span className={latest.blocked ? "badge blocked" : "badge allowed"}>
              {latest.blocked ? "Blocked 🚫" : "Allowed ✔"}
            </span>

            <p className="timestamp">
              {new Date(latest.ts).toLocaleString()}
            </p>
          </>
        ) : (
          <p>No messages yet</p>
        )}
      </div>

      {/* ACTIVITY FEED */}
      <div className="feed-card">
        <h2>Recent Activity</h2>
        <ul>
          {logs.map(l => (
            <li key={l.id}>
              <span className="dot" style={{ background: l.blocked ? "#ff5252" : "#3adb76" }}></span>
              <span className="feed-text">“{l.text}”</span>
              <span className="feed-score">{Math.round(l.toxicity * 100)}%</span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
