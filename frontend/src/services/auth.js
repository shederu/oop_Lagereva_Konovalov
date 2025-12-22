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

  // Вход пользователя (Basic Auth)
  login: async (login, password) => {
    try {
      if (!login || !password) {
        throw new Error('Логин и пароль обязательны');
      }

      // ✅ Создаём Basic Auth token: base64(login:password)
      const basicAuth = btoa(`${login}:${password}`);

      // Сохраняем token в localStorage (НЕ пароль!)
      localStorage.setItem('authToken', basicAuth);
      localStorage.setItem('username', login);

      console.log('Login success for user:', login);
      return {
        token: basicAuth,
        username: login
      };
    } catch (e) {
      console.error('Login error:', e.message);
      throw e;
    }
  },

  // Выход
  logout: () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('username');
    console.log('Logged out');
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
