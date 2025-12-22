import { authService } from './auth';

const API_BASE = 'http://localhost:8080/api';

const getHeaders = () => {
  const token = authService.getToken();
  return {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` })
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
             x: xValues,    // ✅ Правильно - x
             y: yValues     // ✅ Правильно - y
         })
     });
     if (!response.ok) {
         const errorData = await response.json().catch(() => ({ message: 'Ошибка сервера' }));
         throw new Error(errorData.message || 'Ошибка создания функции');
     }
     return response.json();
 },


  /**
   * Создать функцию из другой MathFunction
   */
  createFromFunction: async (functionName, min, max, points) => {
    const response = await fetch(`${API_BASE}/tabulatedFunctions/create-from-function`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify({ functionName, min, max, points })
    });
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({ message: 'Ошибка сервера' }));
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
    if (!response.ok) throw new Error('Ошибка загрузки функций');
    return response.json();
  },

  /**
   * Получить функцию по ID
   */
  getFunctionById: async (id) => {
    const response = await fetch(`${API_BASE}/tabulatedFunctions/${id}`, {
      headers: getHeaders()
    });
    if (!response.ok) throw new Error('Функция не найдена');
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
    if (!response.ok) throw new Error('Ошибка удаления функции');
    return response.json();
  },

  // ==================== COMPOSITE FUNCTIONS (Operations) ====================

  add: async (id1, id2) => {
    const response = await fetch(`${API_BASE}/compositeFunctions/add?id1=${id1}&id2=${id2}`, {
      method: 'POST',
      headers: getHeaders()
    });
    if (!response.ok) throw new Error('Ошибка добавления');
    return response.json();
  },

  subtract: async (id1, id2) => {
    const response = await fetch(`${API_BASE}/compositeFunctions/subtract?id1=${id1}&id2=${id2}`, {
      method: 'POST',
      headers: getHeaders()
    });
    if (!response.ok) throw new Error('Ошибка вычитания');
    return response.json();
  },

  multiply: async (id1, id2) => {
    const response = await fetch(`${API_BASE}/compositeFunctions/multiply?id1=${id1}&id2=${id2}`, {
      method: 'POST',
      headers: getHeaders()
    });
    if (!response.ok) throw new Error('Ошибка умножения');
    return response.json();
  },

  divide: async (id1, id2) => {
    const response = await fetch(`${API_BASE}/compositeFunctions/divide?id1=${id1}&id2=${id2}`, {
      method: 'POST',
      headers: getHeaders()
    });
    if (!response.ok) throw new Error('Ошибка деления');
    return response.json();
  },

  derive: async (id) => {
    const response = await fetch(`${API_BASE}/compositeFunctions/derive?id=${id}`, {
      method: 'POST',
      headers: getHeaders()
    });
    if (!response.ok) throw new Error('Ошибка дифференцирования');
    return response.json();
  }
};
