import React, { useState } from 'react';
import CreateFromArray from './CreateFromArray';
import CreateFromFunction from './CreateFromFunction';
import Operations from './Operations';

export default function MainPage() {
  const [view, setView] = useState('home');

  const navStyle = {
    display: 'flex',
    gap: '10px',
    padding: '20px',
    backgroundColor: '#f8f9fa',
    borderBottom: '1px solid #dee2e6'
  };

  const buttonStyle = {
    padding: '10px 20px',
    backgroundColor: view === 'home' ? '#007bff' : '#6c757d',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer'
  };

  return (
    <div>
      <nav style={navStyle}>
        <button
          onClick={() => setView('home')}
          style={{ ...buttonStyle, backgroundColor: view === 'home' ? '#007bff' : '#6c757d' }}
        >
          Главная
        </button>
        <button
          onClick={() => setView('array')}
          style={{ ...buttonStyle, backgroundColor: view === 'array' ? '#007bff' : '#6c757d' }}
        >
          Создать из массива
        </button>
        <button
          onClick={() => setView('function')}
          style={{ ...buttonStyle, backgroundColor: view === 'function' ? '#007bff' : '#6c757d' }}
        >
          Создать из функции
        </button>
        <button
          onClick={() => setView('operations')}
          style={{ ...buttonStyle, backgroundColor: view === 'operations' ? '#007bff' : '#6c757d' }}
        >
          Операции
        </button>
      </nav>

      <div style={{ padding: '20px', maxWidth: '900px', margin: '0 auto' }}>
        {view === 'home' && (
          <div>
            <h2>Добро пожаловать!</h2>
            <p>Выберите операцию из меню выше</p>
          </div>
        )}
        {view === 'array' && <CreateFromArray />}
        {view === 'function' && <CreateFromFunction />}
        {view === 'operations' && <Operations />}
      </div>
    </div>
  );
}
