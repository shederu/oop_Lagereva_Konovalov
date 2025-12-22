import { authService } from './auth';

const API_BASE = 'http://localhost:8080/api';

// ✅ Получаем заголовки с Basic Auth
const getHeaders = () => {
  const token = authService.getToken();
  return {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Basic ${token}` })
  };
};

export const api = {
  // ==================== TABULATED FUNCTIONS ====================

  /**
   * Создать функцию из массивов X, Y
   */
  createFromArray: async (xValues, yValues, functionName) => {
    const response = await fetch(`${API_BASE}/tabulatedFunctions/createFromArray`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify({
        x: xValues.map(v => parseFloat(v)),
        y: yValues.map(v => parseFloat(v)),
        name: functionName ? functionName.trim() : 'Function'
      })
    });

    if (!response.ok) {
      let errorMessage = 'Ошибка создания функции';

      try {
        // Сначала пытаемся распарсить как JSON
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
          const errorData = await response.json();
          if (errorData.message) {
            errorMessage = errorData.message;
          } else if (errorData.error) {
            errorMessage = errorData.error;
          }
        } else {
          // Если это текст, берём текст как есть
          const errorText = await response.text();
          if (errorText) {
            errorMessage = errorText;
          }
        }
      } catch (e) {
        console.error('Error parsing error response:', e);
      }

      console.log('Server error message:', errorMessage);
      throw new Error(errorMessage);
    }

    return response.json();
  },

  /**
   * Создать функцию из математической функции
   */
  createFromFunction: async (functionName, min, max, points) => {
    const response = await fetch(`${API_BASE}/tabulatedFunctions/create-from-function`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify({
        functionName,
        min: parseFloat(min),
        max: parseFloat(max),
        points: parseInt(points)
      })
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({
        message: 'Ошибка сервера'
      }));
      throw new Error(errorData.message || 'Ошибка создания функции');
    }

    return response.json();
  },

  /**
   * Получить все функции пользователя
   */
  getFunctions: async () => {
    const response = await fetch(`${API_BASE}/tabulatedFunctions`, {
      headers: getHeaders()
    });

    if (!response.ok) {
      if (response.status === 401) {
        authService.logout();
        throw new Error('Сессия истекла. Пожалуйста, заново авторизуйтесь.');
      }
      throw new Error('Ошибка загрузки функций');
    }

    return response.json();
  },

  /**
   * Получить функцию по ID
   */
  getFunctionById: async (id) => {
    const response = await fetch(`${API_BASE}/tabulatedFunctions/${id}`, {
      headers: getHeaders()
    });

    if (!response.ok) {
      if (response.status === 404) {
        throw new Error('Функция не найдена');
      }
      throw new Error('Ошибка загрузки функции');
    }

    return response.json();
  },

  /**
   * Удалить функцию
   */
  deleteFunction: async (id) => {
    const response = await fetch(`${API_BASE}/tabulatedFunctions/${id}`, {
      method: 'DELETE',
      headers: getHeaders()
    });

    if (!response.ok) {
      if (response.status === 404) {
        throw new Error('Функция не найдена');
      }
      throw new Error('Ошибка удаления функции');
    }

    return response.json();
  },

  /**
   * Получить точки функции для графика
   */
  getFunctionPoints: async (id) => {
    const response = await fetch(`${API_BASE}/tabulatedFunctions/${id}/points`, {
      headers: getHeaders()
    });

    if (!response.ok) {
      throw new Error('Ошибка загрузки точек функции');
    }

    return response.json();
  },

  // ==================== COMPOSITE FUNCTIONS (Operations) ====================

  /**
   * Добавить две функции
   */
  add: async (id1, id2) => {
    const response = await fetch(`${API_BASE}/compositeFunctions/add?id1=${id1}&id2=${id2}`, {
      method: 'POST',
      headers: getHeaders()
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({
        message: 'Ошибка сервера'
      }));
      throw new Error(errorData.message || 'Ошибка добавления');
    }

    return response.json();
  },

  /**
   * Вычесть две функции
   */
  subtract: async (id1, id2) => {
    const response = await fetch(`${API_BASE}/compositeFunctions/subtract?id1=${id1}&id2=${id2}`, {
      method: 'POST',
      headers: getHeaders()
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({
        message: 'Ошибка сервера'
      }));
      throw new Error(errorData.message || 'Ошибка вычитания');
    }

    return response.json();
  },

  /**
   * Умножить две функции
   */
  multiply: async (id1, id2) => {
    const response = await fetch(`${API_BASE}/compositeFunctions/multiply?id1=${id1}&id2=${id2}`, {
      method: 'POST',
      headers: getHeaders()
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({
        message: 'Ошибка сервера'
      }));
      throw new Error(errorData.message || 'Ошибка умножения');
    }

    return response.json();
  },

  /**
   * Разделить две функции
   */
  divide: async (id1, id2) => {
    const response = await fetch(`${API_BASE}/compositeFunctions/divide?id1=${id1}&id2=${id2}`, {
      method: 'POST',
      headers: getHeaders()
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({
        message: 'Ошибка сервера'
      }));
      throw new Error(errorData.message || 'Ошибка деления');
    }

    return response.json();
  },

  /**
   * Взять производную функции
   */
  derive: async (id) => {
    const response = await fetch(`${API_BASE}/compositeFunctions/derive?id=${id}`, {
      method: 'POST',
      headers: getHeaders()
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({
        message: 'Ошибка сервера'
      }));
      throw new Error(errorData.message || 'Ошибка дифференцирования');
    }

    return response.json();
  },

  // ==================== SEARCH ====================

  /**
   * Поиск функций
   */
  searchFunctions: async (searchTerm) => {
    const response = await fetch(`${API_BASE}/tabulatedFunctions/search?term=${encodeURIComponent(searchTerm)}`, {
      headers: getHeaders()
    });

    if (!response.ok) {
      throw new Error('Ошибка при поиске');
    }

    return response.json();
  },

  // ==================== RUNGE KUTTA ====================

  /**
   * Решить ОДУ методом Рунге-Кутта
   */
  solveRungeKutta: async (yPrime, y0, x0, xn, h) => {
    const response = await fetch(`${API_BASE}/rungeKutta/solve`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify({
        yPrime,
        y0: parseFloat(y0),
        x0: parseFloat(x0),
        xn: parseFloat(xn),
        h: parseFloat(h)
      })
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({
        message: 'Ошибка сервера'
      }));
      throw new Error(errorData.message || 'Ошибка решения ОДУ');
    }

    return response.json();
  }
};
