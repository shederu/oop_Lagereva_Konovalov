const API_BASE = 'http://localhost:8080/api';

export const authService = {
  register: async (login, password) => {
    try {
      if (!login || !password) {
        throw new Error('Логин и пароль обязательны');
      }

      const response = await fetch(`${API_BASE}/users/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ login, password })
      });

      console.log('Register - Response status:', response.status);
      const text = await response.text();
      console.log('Register - Response body:', text);

      if (!response.ok) {
        throw new Error(`Ошибка регистрации: ${text}`);
      }

      return JSON.parse(text);
    } catch (e) {
      console.error('Register error:', e.message);
      throw e;
    }
  },

  login: async (login, password) => {
    try {
      if (!login || !password) {
        throw new Error('Логин и пароль обязательны');
      }

      // ✅ Используем новый эндпоинт /login
      const response = await fetch(`${API_BASE}/users/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ login, password })
      });

      console.log('Login - Response status:', response.status);
      const text = await response.text();
      console.log('Login - Response body:', text);

      if (response.status === 401 || response.status === 403) {
        throw new Error('Неверные логин или пароль');
      }

      if (!response.ok) {
        throw new Error('Ошибка при входе: ' + response.status);
      }

      const data = JSON.parse(text);

      // ✅ Создаём token для хранения
      const token = btoa(`${login}:${password}`);
      localStorage.setItem('authToken', token);
      localStorage.setItem('username', login);

      console.log('Login success for user:', login);
      return { token, username: login };
    } catch (e) {
      console.error('Login error:', e.message);
      throw e;
    }
  },

  logout: () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('username');
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('authToken');
  },

  getToken: () => {
    return localStorage.getItem('authToken');
  },

  getUsername: () => {
    return localStorage.getItem('username');
  }
};
