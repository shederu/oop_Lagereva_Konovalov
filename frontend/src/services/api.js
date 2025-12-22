import { authService } from './auth';

const API_BASE = 'http://localhost:8080/api';

const getHeaders = () => {
  const token = authService.getToken();
  return {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Basic ${token}` })
  };
};

export const api = {
  // ==================== TABULATED FUNCTIONS ====================

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
      // ✅ ИСПРАВЛЕНИЕ: Правильно парсим ошибку
      let errorMessage = 'Ошибка сервера';

      try {
        const errorData = await response.json();
        console.log('Server error response:', errorData); // Для отладки

        // Проверяем разные варианты структуры ошибки
        if (errorData.message) {
          errorMessage = errorData.message;
        } else if (errorData.error) {
          errorMessage = errorData.error;
        } else if (typeof errorData === 'string') {
          errorMessage = errorData;
        }
      } catch (e) {
        // Если не JSON, берём статус текст
        errorMessage = response.statusText || 'Ошибка сервера';
      }

      throw new Error(errorMessage);
    }

    return response.json();
  },

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
      let errorMessage = 'Ошибка сервера';

      try {
        const errorData = await response.json();
        if (errorData.message) {
          errorMessage = errorData.message;
        } else if (errorData.error) {
          errorMessage = errorData.error;
        }
      } catch (e) {
        errorMessage = response.statusText || 'Ошибка сервера';
      }

      throw new Error(errorMessage);
    }

    return response.json();
  },

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

  add: async (id1, id2) => {
    const response = await fetch(`${API_BASE}/compositeFunctions/add?id1=${id1}&id2=${id2}`, {
      method: 'POST',
      headers: getHeaders()
    });

    if (!response.ok) {
      let errorMessage = 'Ошибка добавления';
      try {
        const errorData = await response.json();
        if (errorData.message) errorMessage = errorData.message;
      } catch (e) {}
      throw new Error(errorMessage);
    }

    return response.json();
  },

  subtract: async (id1, id2) => {
    const response = await fetch(`${API_BASE}/compositeFunctions/subtract?id1=${id1}&id2=${id2}`, {
      method: 'POST',
      headers: getHeaders()
    });

    if (!response.ok) {
      let errorMessage = 'Ошибка вычитания';
      try {
        const errorData = await response.json();
        if (errorData.message) errorMessage = errorData.message;
      } catch (e) {}
      throw new Error(errorMessage);
    }

    return response.json();
  },

  multiply: async (id1, id2) => {
    const response = await fetch(`${API_BASE}/compositeFunctions/multiply?id1=${id1}&id2=${id2}`, {
      method: 'POST',
      headers: getHeaders()
    });

    if (!response.ok) {
      let errorMessage = 'Ошибка умножения';
      try {
        const errorData = await response.json();
        if (errorData.message) errorMessage = errorData.message;
      } catch (e) {}
      throw new Error(errorMessage);
    }

    return response.json();
  },

  divide: async (id1, id2) => {
    const response = await fetch(`${API_BASE}/compositeFunctions/divide?id1=${id1}&id2=${id2}`, {
      method: 'POST',
      headers: getHeaders()
    });

    if (!response.ok) {
      let errorMessage = 'Ошибка деления';
      try {
        const errorData = await response.json();
        if (errorData.message) errorMessage = errorData.message;
      } catch (e) {}
      throw new Error(errorMessage);
    }

    return response.json();
  },

  derive: async (id) => {
    const response = await fetch(`${API_BASE}/compositeFunctions/derive?id=${id}`, {
      method: 'POST',
      headers: getHeaders()
    });

    if (!response.ok) {
      let errorMessage = 'Ошибка дифференцирования';
      try {
        const errorData = await response.json();
        if (errorData.message) errorMessage = errorData.message;
      } catch (e) {}
      throw new Error(errorMessage);
    }

    return response.json();
  },

  // ==================== SEARCH ====================

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
      let errorMessage = 'Ошибка решения ОДУ';
      try {
        const errorData = await response.json();
        if (errorData.message) errorMessage = errorData.message;
      } catch (e) {}
      throw new Error(errorMessage);
    }

    return response.json();
  }
};
