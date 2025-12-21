const API_BASE = 'http://localhost:8080/api';

export const authService = {
  register: async (login, password) => {
    try {
      if (!login || !password) {
        throw new Error('Логин и пароль обязательны');
      }

      const body = JSON.stringify({ login, password });
      console.log('Register - Sending:', body);

      const response = await fetch(`${API_BASE}/users/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: body
      });

      console.log('Register - Response status:', response.status);
      const text = await response.text();
      console.log('Register - Response body:', text);

      if (!response.ok) {
        throw new Error(`Ошибка регистрации: ${text}`);
      }

      const data = JSON.parse(text);
      console.log('Register success:', data);
      return data;
    } catch (e) {
      console.error('Register error:', e.message);
      throw e;
    }
  },

  // ✅ ИСПРАВЛЕННАЯ функция логина - ПРОВЕРЯЕТ с сервером
  login: async (login, password) => {
    try {
      if (!login || !password) {
        throw new Error('Логин и пароль обязательны');
      }

      const token = btoa(`${login}:${password}`);

      // ✅ ПРОВЕРЯЕМ на сервере через Basic Auth
      const response = await fetch(`${API_BASE}/users`, {
        method: 'GET',
        headers: {
          'Authorization': `Basic ${token}`,
          'Content-Type': 'application/json'
        }
      });

      console.log('Login - Response status:', response.status);

      if (response.status === 401 || response.status === 403) {
        throw new Error('Неверные логин или пароль');
      }

      if (!response.ok) {
        throw new Error('Ошибка при входе: ' + response.status);
      }

      // ✅ Если сервер вернул 200 - пользователь существует и пароль верный
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
    try {
      localStorage.removeItem('authToken');
      localStorage.removeItem('username');
      console.log('Logout success');
    } catch (e) {
      console.error('Logout error:', e.message);
    }
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
