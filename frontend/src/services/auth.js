const API_BASE = 'http://localhost:8080/api';

export const authService = {

  // Регистрация нового пользователя
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

  // ✅ Вход пользователя БЕЗ авто‑регистрации, с проверкой пароля
  login: async (login, password) => {
    try {
      if (!login || !password) {
        throw new Error('Логин и пароль обязательны');
      }

      // ⛔ Никакой регистрации здесь — только проверка логина/пароля
      const response = await fetch(`${API_BASE}/users/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ login, password })
      });

      console.log('Login - Response status:', response.status);
      const data = await response.json().catch(() => ({}));
      console.log('Login - Response body:', data);

      if (!response.ok) {
        throw new Error(data.error || 'Ошибка при входе');
      }

      // ✅ Создаём Basic Auth token ТОЛЬКО после успешного логина
      const basicAuth = btoa(`${login}:${password}`);

      // Сохраняем token в localStorage (НЕ пароль!)
      localStorage.setItem('authToken', basicAuth);
      localStorage.setItem('username', login);

      console.log('✓ Login success for user:', login);
      console.log('🔑 Auth token:', basicAuth);

      return {
        token: basicAuth,
        username: login
      };
    } catch (e) {
      console.error('❌ Login error:', e.message);
      throw e;
    }
  },

  // Выход
  logout: () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('username');
    console.log('✓ Logged out');
  },

  // Проверка аутентификации
  isAuthenticated: () => {
    return !!localStorage.getItem('authToken');
  },

  // Получить token
  getToken: () => {
    return localStorage.getItem('authToken');
  },

  // Получить username
  getUsername: () => {
    return localStorage.getItem('username');
  }
};
