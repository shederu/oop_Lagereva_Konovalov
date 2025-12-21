import React, { useState } from 'react';
import { api } from '../services/api';

export default function CreateFromArray() {
  const [size, setSize] = useState('');
  const [points, setPoints] = useState([]);
  const [error, setError] = useState('');

  const createTable = () => {
    try {
      const n = parseInt(size);
      if (n <= 0) throw new Error('Размер должен быть положительным');
      setPoints(Array(n).fill().map(() => ({ x: '', y: '' })));
      setError('');
    } catch (e) {
      setError(e.message);
    }
  };

  const updatePoint = (index, field, value) => {
    const newPoints = [...points];
    newPoints[index][field] = value;
    setPoints(newPoints);
  };

  const createFunction = async () => {
    try {
      const x = points.map(p => parseFloat(p.x));
      const y = points.map(p => parseFloat(p.y));

      if (x.some(isNaN) || y.some(isNaN)) {
        throw new Error('Все значения должны быть числами');
      }

      const result = await api.createFromArray(x, y);
      alert('Функция создана!');
      setPoints([]);
      setSize('');
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>Создать функцию из массива</h2>

      <div>
        <label>Количество точек: </label>
        <input
          type="number"
          value={size}
          onChange={e => setSize(e.target.value)}
        />
        <button onClick={createTable}>Создать таблицу</button>
      </div>

      {points.length > 0 && (
        <div style={{ marginTop: '20px' }}>
          <table style={{ border: '1px solid #ccc' }}>
            <thead>
              <tr>
                <th style={{ padding: '10px', border: '1px solid #ccc' }}>X</th>
                <th style={{ padding: '10px', border: '1px solid #ccc' }}>Y</th>
              </tr>
            </thead>
            <tbody>
              {points.map((point, i) => (
                <tr key={i}>
                  <td style={{ padding: '10px', border: '1px solid #ccc' }}>
                    <input
                      type="number"
                      value={point.x}
                      onChange={e => updatePoint(i, 'x', e.target.value)}
                    />
                  </td>
                  <td style={{ padding: '10px', border: '1px solid #ccc' }}>
                    <input
                      type="number"
                      value={point.y}
                      onChange={e => updatePoint(i, 'y', e.target.value)}
                    />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <button onClick={createFunction} style={{ marginTop: '10px' }}>
            Создать функцию
          </button>
        </div>
      )}

      {error && <div style={{ color: 'red', marginTop: '10px' }}>{error}</div>}
    </div>
  );
}
