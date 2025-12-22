import React, { useState } from 'react';
import { api } from '../services/api';

const FUNCTIONS = {
  'sqr': 'Квадратичная функция',
  'identity': 'Тождественная функция',
  'zero': 'Нулевая функция',
  'unit': 'Единичная функция',
  'constant': 'Постоянная функция',
  'sin': 'Синус',
  'cos': 'Косинус',
  'ln': 'Натуральный логарифм',
};

export default function CreateFromFunction() {
  const [func, setFunc] = useState('sqr');
  const [min, setMin] = useState('');
  const [max, setMax] = useState('');
  const [points, setPoints] = useState('');
  const [error, setError] = useState('');

  const createFunction = async () => {
    try {
      const p = parseInt(points);
      const minVal = parseFloat(min);
      const maxVal = parseFloat(max);

      if (isNaN(p) || p <= 0) throw new Error('Количество точек должно быть положительным числом');
      if (isNaN(minVal) || isNaN(maxVal)) throw new Error('Интервал должен содержать числа');
      if (minVal >= maxVal) throw new Error('Начало интервала должно быть меньше конца');

      const result = await api.createFromFunction(func, minVal, maxVal, p);
      alert('Функция создана!');
      setFunc('sqr');
      setMin('');
      setMax('');
      setPoints('');
      setError('');
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>Создать функцию</h2>

      <div style={{ marginBottom: '10px' }}>
        <label>Функция: </label>
        <select value={func} onChange={e => setFunc(e.target.value)}>
          {Object.entries(FUNCTIONS).map(([key, name]) => (
            <option key={key} value={key}>{name}</option>
          ))}
        </select>
      </div>

      <div style={{ marginBottom: '10px' }}>
        <label>Количество точек: </label>
        <input type="number" value={points} onChange={e => setPoints(e.target.value)} />
      </div>

      <div style={{ marginBottom: '10px' }}>
        <label>Начало интервала: </label>
        <input type="number" value={min} onChange={e => setMin(e.target.value)} />
      </div>

      <div style={{ marginBottom: '10px' }}>
        <label>Конец интервала: </label>
        <input type="number" value={max} onChange={e => setMax(e.target.value)} />
      </div>

      <button onClick={createFunction}>Создать</button>

      {error && <div style={{ color: 'red', marginTop: '10px' }}>{error}</div>}
    </div>
  );
}
