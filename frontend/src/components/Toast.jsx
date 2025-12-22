import React, { useEffect, useState } from 'react';

export default function Toast({ message, type = 'success', duration = 3000, onClose }) {
  const [isVisible, setIsVisible] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsVisible(false);
      setTimeout(onClose, 300); // Даём время на анимацию
    }, duration);

    return () => clearTimeout(timer);
  }, [duration, onClose]);

  const colors = {
    success: { bg: '#d4edda', border: '#28a745', icon: '✓', color: '#155724' },
    error: { bg: '#f8d7da', border: '#dc3545', icon: '✕', color: '#721c24' },
    warning: { bg: '#fff3cd', border: '#ffc107', icon: '⚠', color: '#856404' },
    info: { bg: '#d1ecf1', border: '#0c5460', icon: 'ℹ', color: '#0c5460' }
  };

  const style = colors[type];

  return (
    <div
      style={{
        position: 'fixed',
        top: '20px',
        right: '20px',
        zIndex: 10000,
        animation: isVisible ? 'slideIn 0.3s ease-out' : 'slideOut 0.3s ease-out',
        transformOrigin: 'right center',
      }}
    >
      <style>{`
        @keyframes slideIn {
          from {
            opacity: 0;
            transform: translateX(400px);
          }
          to {
            opacity: 1;
            transform: translateX(0);
          }
        }
        @keyframes slideOut {
          from {
            opacity: 1;
            transform: translateX(0);
          }
          to {
            opacity: 0;
            transform: translateX(400px);
          }
        }
      `}</style>

      <div
        style={{
          backgroundColor: style.bg,
          border: `2px solid ${style.border}`,
          borderRadius: '8px',
          padding: '16px 20px',
          display: 'flex',
          alignItems: 'center',
          gap: '12px',
          minWidth: '320px',
          maxWidth: '450px',
          boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)',
          fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif',
        }}
      >
        <div
          style={{
            fontSize: '24px',
            color: style.border,
            fontWeight: 'bold',
            flexShrink: 0,
          }}
        >
          {style.icon}
        </div>

        <div
          style={{
            flex: 1,
            color: style.color,
            fontSize: '14px',
            fontWeight: '500',
            lineHeight: '1.4',
          }}
        >
          {message}
        </div>

        <button
          onClick={() => {
            setIsVisible(false);
            setTimeout(onClose, 300);
          }}
          style={{
            background: 'none',
            border: 'none',
            color: style.color,
            fontSize: '20px',
            cursor: 'pointer',
            padding: '0',
            opacity: 0.6,
            transition: 'opacity 0.2s',
          }}
          onMouseEnter={(e) => e.target.style.opacity = '1'}
          onMouseLeave={(e) => e.target.style.opacity = '0.6'}
        >
          ✕
        </button>
      </div>
    </div>
  );
}
