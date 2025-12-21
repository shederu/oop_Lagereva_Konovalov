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
  createFromArray: async (x, y) => {
    const response = await fetch(`${API_BASE}/tabulatedFunctions/createFromArray`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify({ x, y })
    });
    if (!response.ok) throw new Error('Ошибка создания функции');
    return response.json();
  },

  createFromFunction: async (functionName, min, max, points) => {
    const response = await fetch(`${API_BASE}/tabulatedFunctions/createFromFunction`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify({ functionName, min, max, points })
    });
    if (!response.ok) throw new Error('Ошибка создания функции');
    return response.json();
  },

  getFunctions: async () => {
    const response = await fetch(`${API_BASE}/tabulatedFunctions`, {
      headers: getHeaders()
    });
    if (!response.ok) throw new Error('Ошибка загрузки функций');
    return response.json();
  },

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
