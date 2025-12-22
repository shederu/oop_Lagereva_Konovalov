import React, { useState } from 'react';
import { api } from '../services/api';

export default function CreateFromArray({ onClose, onSuccess, existingFunctions = [] }) {
  const [size, setSize] = useState('');
  const [functionName, setFunctionName] = useState('');
  const [points, setPoints] = useState([]);
  const [showTable, setShowTable] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleGenerateTable = () => {
    setError('');
    const n = parseFloat(size);

    if (isNaN(n) || n <= 0) {
      setError('Введите положительное число');
      return;
    }

    if (!Number.isInteger(n)) {
      setError('Количество точек должно быть целым числом');
      return;
    }

    if (n < 2) {
      setError('Минимум 2 точки');
      return;
    }

    setPoints(Array(n).fill(null).map(() => ({ x: '', y: '' })));
    setShowTable(true);
  };

  const handleUpdatePoint = (index, field, value) => {
    const newPoints = [...points];
    newPoints[index][field] = value;
    setPoints(newPoints);
  };

  const validatePoints = () => {
    for (let i = 0; i < points.length; i++) {
      if (points[i].x === '' || points[i].y === '') {
        return `Точка ${i + 1}: заполните оба поля`;
      }
    }

    for (let i = 1; i < points.length; i++) {
      if (parseFloat(points[i].x) <= parseFloat(points[i - 1].x)) {
        return `Ошибка: X${i} (${points[i].x}) должен быть больше X${i - 1} (${points[i - 1].x})`;
      }
    }

    return null;
  };

  // ✅ НОВАЯ ФУНКЦИЯ: Проверка на дубликаты
  const validateFunctionName = () => {
    const nameExists = existingFunctions.some(
      f => f.name?.toLowerCase() === functionName.trim().toLowerCase()
    );
    if (nameExists) {
      setError(`⚠️ Функция "${functionName}" уже существует!`);
      return false;
    }
    return true;
  };

  const handleCreateFunction = async () => {
    setError('');

    if (!functionName.trim()) {
      setError('Введите имя функции');
      return;
    }

    // ✅ ПРОВЕРКА НА ДУБЛИКАТЫ ПЕРЕД ОТПРАВКОЙ!
    if (!validateFunctionName()) {
      return;
    }

    const pointsErr = validatePoints();
    if (pointsErr) {
      setError(pointsErr);
      return;
    }

    setLoading(true);

    try {
      const xArray = points.map(p => parseFloat(p.x));
      const yArray = points.map(p => parseFloat(p.y));

      await api.createFromArray(xArray, yArray, functionName.trim());

      alert('✓ Функция успешно создана!');

      if (onSuccess) onSuccess();
      onClose();
    } catch (err) {
      setError(err.message || 'Ошибка сервера');
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      {/* Overlay - тёмный фон */}
      <div style={{
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: 'rgba(0, 0, 0, 0.5)',
        zIndex: 999
      }} onClick={onClose} />

      {/* Модальное окно */}
      <div style={{
        position: 'fixed',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)',
        maxWidth: '700px',
        width: '90%',
        maxHeight: '90vh',
        overflowY: 'auto',
        border: '1px solid #ddd',
        padding: '20px',
        borderRadius: '8px',
        background: '#fff',
        boxShadow: '0 4px 20px rgba(0, 0, 0, 0.3)',
        zIndex: 1000
      }}>
        <h2 style={{ marginTop: 0 }}>Создать функцию из массива</h2>

        {error && (
          <div style={{ color: '#721c24', backgroundColor: '#f8d7da', padding: '10px', marginBottom: '15px', borderRadius: '4px' }}>
            {error}
          </div>
        )}

        {!showTable ? (
          <div>
            <div style={{ marginBottom: '15px' }}>
              <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Имя функции:</label>
              <input
                type="text"
                value={functionName}
                onChange={(e) => setFunctionName(e.target.value)}
                placeholder="Например: TestFunc"
                style={{ width: '100%', padding: '8px', boxSizing: 'border-box', border: '1px solid #ccc', borderRadius: '4px' }}
              />
            </div>

            <div style={{ marginBottom: '15px' }}>
              <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Количество точек:</label>
              <input
                type="number"
                value={size}
                onChange={(e) => setSize(e.target.value)}
                placeholder="Ваше число"
                style={{ width: '100%', padding: '8px', boxSizing: 'border-box', border: '1px solid #ccc', borderRadius: '4px' }}
              />
            </div>

            <button
              onClick={handleGenerateTable}
              style={{ padding: '10px 20px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
            >
              Далее
            </button>
          </div>
        ) : (
          <div>
            <div style={{ marginBottom: '15px', maxHeight: '400px', overflowY: 'auto', border: '1px solid #eee', borderRadius: '4px' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                  <tr style={{ background: '#f8f9fa' }}>
                    <th style={{ padding: '8px', borderBottom: '1px solid #ddd', textAlign: 'left' }}>#</th>
                    <th style={{ padding: '8px', borderBottom: '1px solid #ddd', textAlign: 'left' }}>X</th>
                    <th style={{ padding: '8px', borderBottom: '1px solid #ddd', textAlign: 'left' }}>Y</th>
                  </tr>
                </thead>
                <tbody>
                  {points.map((p, idx) => (
                    <tr key={idx}>
                      <td style={{ padding: '8px', borderBottom: '1px solid #eee', textAlign: 'center' }}>{idx + 1}</td>
                      <td style={{ padding: '8px', borderBottom: '1px solid #eee' }}>
                        <input
                          type="number"
                          value={p.x}
                          onChange={(e) => handleUpdatePoint(idx, 'x', e.target.value)}
                          placeholder="X"
                          style={{ padding: '6px', width: '90%', border: '1px solid #ccc', borderRadius: '4px' }}
                        />
                      </td>
                      <td style={{ padding: '8px', borderBottom: '1px solid #eee' }}>
                        <input
                          type="number"
                          value={p.y}
                          onChange={(e) => handleUpdatePoint(idx, 'y', e.target.value)}
                          placeholder="Y"
                          style={{ padding: '6px', width: '90%', border: '1px solid #ccc', borderRadius: '4px' }}
                        />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div style={{ display: 'flex', gap: '10px' }}>
              <button
                onClick={handleCreateFunction}
                disabled={loading}
                style={{ padding: '10px 20px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
              >
                {loading ? 'Сохранение...' : 'Создать'}
              </button>

              <button
                onClick={() => setShowTable(false)}
                style={{ padding: '10px 20px', backgroundColor: '#6c757d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
              >
                Назад
              </button>

              <button
                onClick={onClose}
                style={{ padding: '10px 20px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', marginLeft: 'auto' }}
              >
                Закрыть
              </button>
            </div>
          </div>
        )}
      </div>
    </>
  );
}
