import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

export default function Operations() {
  const [functions, setFunctions] = useState([]);
  const [selectedOp, setSelectedOp] = useState('add');
  const [id1, setId1] = useState('');
  const [id2, setId2] = useState('');
  const [error, setError] = useState('');
  const [result, setResult] = useState(null);

  useEffect(() => {
    loadFunctions();
  }, []);

  const loadFunctions = async () => {
    try {
      const data = await api.getFunctions();
      setFunctions(data.functions || []);
    } catch (e) {
      setError('Ошибка загрузки функций');
    }
  };

  const performOperation = async () => {
    try {
      if (!id1 || !id2) throw new Error('Выберите две функции');

      let res;
      switch (selectedOp) {
        case 'add':
          res = await api.add(id1, id2);
          break;
        case 'subtract':
          res = await api.subtract(id1, id2);
          break;
        case 'multiply':
          res = await api.multiply(id1, id2);
          break;
        case 'divide':
          res = await api.divide(id1, id2);
          break;
        default:
          throw new Error('Неизвестная операция');
      }

      setResult(res);
      setError('');
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <div>
      <h2>Операции с функциями</h2>

      <div style={{ marginBottom: '15px' }}>
        <label>Операция: </label>
        <select value={selectedOp} onChange={e => setSelectedOp(e.target.value)}>
          <option value="add">Сложить (+)</option>
          <option value="subtract">Вычесть (-)</option>
          <option value="multiply">Умножить (*)</option>
          <option value="divide">Разделить (/)</option>
        </select>
      </div>

      <div style={{ marginBottom: '15px' }}>
        <label>Первая функция: </label>
        <select value={id1} onChange={e => setId1(e.target.value)}>
          <option value="">Выберите функцию</option>
          {functions.map(f => (
            <option key={f.id} value={f.id}>{f.name}</option>
          ))}
        </select>
      </div>

      <div style={{ marginBottom: '15px' }}>
        <label>Вторая функция: </label>
        <select value={id2} onChange={e => setId2(e.target.value)}>
          <option value="">Выберите функцию</option>
          {functions.map(f => (
            <option key={f.id} value={f.id}>{f.name}</option>
          ))}
        </select>
      </div>

      <button
        onClick={performOperation}
        style={{
          padding: '10px 20px',
          backgroundColor: '#28a745',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: 'pointer'
        }}
      >
        Выполнить операцию
      </button>

      {error && (
        <div style={{ color: 'red', marginTop: '15px', padding: '10px', backgroundColor: '#ffe0e0', borderRadius: '4px' }}>
          {error}
        </div>
      )}

      {result && (
        <div style={{ marginTop: '20px', padding: '15px', backgroundColor: '#e8f5e9', borderRadius: '4px' }}>
          <h3>Результат:</h3>
          <p><strong>Статус:</strong> {result.status}</p>
          <p><strong>Операция:</strong> {result.operation}</p>
        </div>
      )}
    </div>
  );
}
