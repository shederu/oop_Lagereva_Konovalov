import React, { useState, useEffect } from 'react';
import { authService } from './services/auth';
import LoginPage from './components/LoginPage';
import MainPage from './components/MainPage';

export default function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [username, setUsername] = useState('');

  useEffect(() => {
    // Проверяем, есть ли уже авторизация
    if (authService.isAuthenticated()) {
      setIsAuthenticated(true);
      setUsername(authService.getUsername());
    }
  }, []);

  const handleLoginSuccess = (user) => {
    setIsAuthenticated(true);
    setUsername(user);
  };

  const handleLogout = () => {
    authService.logout();
    setIsAuthenticated(false);
    setUsername('');
  };

  if (!isAuthenticated) {
    return <LoginPage onLoginSuccess={handleLoginSuccess} />;
  }

  return (
    <div>
      <div style={{
        backgroundColor: '#f8f9fa',
        padding: '15px',
        borderBottom: '1px solid #dee2e6',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
      }}>
        <h1>LR7 - Табулированные функции</h1>
        <div>
          <span style={{ marginRight: '15px' }}>Пользователь: <strong>{username}</strong></span>
          <button
            onClick={handleLogout}
            style={{
              padding: '8px 16px',
              backgroundColor: '#dc3545',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            Выход
          </button>
        </div>
      </div>

      <MainPage />
    </div>
  );
}
