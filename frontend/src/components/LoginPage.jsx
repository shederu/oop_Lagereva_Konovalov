import React, { useState } from 'react';
import { authService } from '../services/auth';

export default function LoginPage({ onLoginSuccess }) {
  const [login, setLogin] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isRegistering, setIsRegistering] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      if (!login || !password) {
        throw new Error('Введите логин и пароль');
      }
      console.log('Попытка входа:', login);
      const result = await authService.login(login, password);
      console.log('Вход успешен:', result);
      onLoginSuccess(result.username);
      setError('');
    } catch (e) {
      console.error('Ошибка входа:', e.message);
      setError(e.message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      if (!login || !password) {
        throw new Error('Введите логин и пароль');
      }
      if (password.length < 6) {
        throw new Error('Пароль должен быть не менее 6 символов');
      }

      console.log('Попытка регистрации:', login);
      const registerResult = await authService.register(login, password);
      console.log('Регистрация успешна:', registerResult);

      const loginResult = await authService.login(login, password);
      console.log('Автоматический вход:', loginResult);
      onLoginSuccess(loginResult.username);
      setError('');
      setIsRegistering(false);
    } catch (e) {
      console.error('Ошибка регистрации:', e.message);
      setError(e.message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: '50px auto', padding: '20px' }}>
      <h1>Калькулятор онлайн</h1>

      <form onSubmit={isRegistering ? handleRegister : handleLogin}>
        <h2>{isRegistering ? 'Регистрация' : 'Вход'}</h2>

        <div style={{ marginBottom: '15px' }}>
          <label>Логин:</label>
          <input
            type="text"
            value={login}
            onChange={e => setLogin(e.target.value)}
            placeholder="Введите логин"
            style={{
              width: '100%',
              padding: '8px',
              marginTop: '5px',
              boxSizing: 'border-box',
              border: '1px solid #ccc',
              borderRadius: '4px'
            }}
          />
        </div>

        <div style={{ marginBottom: '15px' }}>
          <label>Пароль:</label>
          <input
            type="password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            placeholder="Введите пароль"
            style={{
              width: '100%',
              padding: '8px',
              marginTop: '5px',
              boxSizing: 'border-box',
              border: '1px solid #ccc',
              borderRadius: '4px'
            }}
          />
        </div>

        {error && (
          <div style={{
            color: 'red',
            marginBottom: '15px',
            padding: '10px',
            backgroundColor: '#ffe0e0',
            borderRadius: '4px',
            fontSize: '14px'
          }}>
            ⚠️ {error}
          </div>
        )}

        <button
          type="submit"
          disabled={isLoading}
          style={{
            width: '100%',
            padding: '10px',
            backgroundColor: isLoading ? '#ccc' : '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: isLoading ? 'not-allowed' : 'pointer',
            marginBottom: '10px',
            fontWeight: 'bold'
          }}
        >
          {isLoading ? 'Загрузка...' : (isRegistering ? 'Зарегистрироваться' : 'Войти')}
        </button>

        <button
          type="button"
          onClick={() => {
            setIsRegistering(!isRegistering);
            setError('');
          }}
          disabled={isLoading}
          style={{
            width: '100%',
            padding: '10px',
            backgroundColor: '#6c757d',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: isLoading ? 'not-allowed' : 'pointer'
          }}
        >
          {isRegistering ? 'Уже есть аккаунт? Вход' : 'Нет аккаунта? Регистрация'}
        </button>
      </form>
    </div>
  );
}
