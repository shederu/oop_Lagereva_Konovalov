import React, { useState } from 'react';
import { api } from '../services/api';

export default function CreateFromArray({ onClose, onSuccess }) {
  const [size, setSize] = useState('');
  const [functionName, setFunctionName] = useState('');
  const [points, setPoints] = useState([]);
  const [showTable, setShowTable] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const [sizeError, setSizeError] = useState('');
  const [nameError, setNameError] = useState('');

  const handleGenerateTable = () => {
    setSizeError('');

    if (!size.trim()) {
      setSizeError('Пожалуйста, введите количество точек');
      return;
    }

    const n = parseInt(size, 10);

    if (isNaN(n)) {
      setSizeError('Должно быть число');
      return;
    }

    if (n <= 0) {
      setSizeError('Количество точек должно быть положительным');
      return;
    }

    if (n > 10000) {
      setSizeError('Слишком много точек (макс 10000)');
      return;
    }

    setPoints(Array(n).fill(null).map(() => ({ x: '', y: '' })));
    setShowTable(true);
    setSizeError('');
  };

  const handleUpdatePoint = (index, field, value) => {
    const newPoints = [...points];
    newPoints[index][field] = value;
    setPoints(newPoints);
  };

  const validatePoints = () => {
    for (let i = 0; i < points.length; i++) {
      const { x, y } = points[i];

      if (!x.trim() || !y.trim()) {
        return `Точка ${i + 1}: оба значения должны быть заполнены`;
      }

      const xNum = parseFloat(x);
      const yNum = parseFloat(y);

      if (isNaN(xNum) || isNaN(yNum)) {
        return `Точка ${i + 1}: значения должны быть числами`;
      }
    }

    for (let i = 1; i < points.length; i++) {
      const prevX = parseFloat(points[i - 1].x);
      const currX = parseFloat(points[i].x);
      if (prevX >= currX) {
        return `Значения X должны быть в возрастающем порядке (ошибка между точками ${i} и ${i + 1})`;
      }
    }

    return null;
  };

  const validateFunctionName = () => {
    if (!functionName.trim()) {
      return 'Пожалуйста, введите имя функции';
    }
    if (functionName.trim().length < 2) {
      return 'Имя функции должно содержать минимум 2 символа';
    }
    if (functionName.trim().length > 50) {
      return 'Имя функции не должно превышать 50 символов';
    }
    return null;
  };

  const handleCreateFunction = async () => {
    // Валидация имени
    const nameValidationError = validateFunctionName();
    if (nameValidationError) {
      setNameError(nameValidationError);
      return;
    }

    // Валидация точек
    const validationError = validatePoints();
    if (validationError) {
      setError(validationError);
      return;
    }

    setLoading(true);
    setNameError('');
    setError('');
    try {
      const xArray = points.map(p => parseFloat(p.x));
      const yArray = points.map(p => parseFloat(p.y));

      const response = await api.createFromArray(xArray, yArray, functionName.trim());

      setSuccess('✓ Функция успешно создана!');
      setPoints([]);
      setSize('');
      setFunctionName('');
      setShowTable(false);

      if (onSuccess) {
        onSuccess(response);
      }

      setTimeout(() => {
        if (onClose) onClose();
      }, 2000);
    } catch (err) {
      setError(err.message || 'Ошибка при создании функции');
    } finally {
      setLoading(false);
    }
  };

  const containerStyle = {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
    zIndex: 1000
  };

  const modalStyle = {
    backgroundColor: '#ffffff',
    borderRadius: '8px',
    padding: '30px',
    width: '90%',
    maxWidth: '700px',
    maxHeight: '90vh',
    overflowY: 'auto',
    boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
    border: '1px solid #dee2e6'
  };

  const inputStyle = {
    padding: '10px 12px',
    marginRight: '10px',
    border: '1px solid #dee2e6',
    borderRadius: '4px',
    backgroundColor: '#f8f9fa',
    color: '#212529',
    fontSize: '14px'
  };

  const buttonStyle = {
    padding: '10px 20px',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontWeight: '500'
  };

  const errorStyle = {
    color: '#721c24',
    fontSize: '13px',
    marginTop: '6px',
    padding: '8px 12px',
    backgroundColor: '#f8d7da',
    borderRadius: '4px',
    border: '1px solid #f5c6cb'
  };

  const successStyle = {
    color: '#155724',
    fontSize: '13px',
    marginTop: '6px',
    padding: '8px 12px',
    backgroundColor: '#d4edda',
    borderRadius: '4px',
    border: '1px solid #c3e6cb'
  };

  const labelStyle = {
    color: '#495057',
    display: 'block',
    marginBottom: '8px',
    fontWeight: '500',
    fontSize: '14px'
  };

  const sectionStyle = {
    marginBottom: '20px'
  };

  return (
    <div style={containerStyle} onClick={onClose}>
      <div style={modalStyle} onClick={(e) => e.stopPropagation()}>
        <h2 style={{ color: '#212529', marginTop: 0 }}>Создать функцию из массивов</h2>

        {/* Шаг 1: Имя функции */}
        <div style={sectionStyle}>
          <label htmlFor="function-name" style={labelStyle}>
            Имя функции:
          </label>
          <input
            id="function-name"
            type="text"
            value={functionName}
            onChange={(e) => {
              setFunctionName(e.target.value);
              setNameError('');
            }}
            placeholder="Например: Моя функция"
            disabled={loading}
            style={{...inputStyle, marginRight: 0, width: '100%', boxSizing: 'border-box'}}
          />
          {nameError && <div style={errorStyle}>{nameError}</div>}
        </div>

        {/* Шаг 2: Количество точек */}
        <div style={sectionStyle}>
          <label htmlFor="points-count" style={labelStyle}>
            Количество точек:
          </label>
          <div style={{ display: 'flex', gap: '10px' }}>
            <input
              id="points-count"
              type="number"
              value={size}
              onChange={(e) => {
                setSize(e.target.value);
                setSizeError('');
              }}
              placeholder="Введите число"
              min="1"
              max="10000"
              disabled={loading}
              style={{...inputStyle, marginRight: 0, flex: 1}}
            />
            <button
              onClick={handleGenerateTable}
              disabled={!size || loading}
              style={{...buttonStyle, opacity: (!size || loading) ? 0.6 : 1}}
            >
              Сгенерировать
            </button>
          </div>
          {sizeError && <div style={errorStyle}>{sizeError}</div>}
        </div>

        {/* Шаг 3: Таблица для ввода X и Y */}
        {showTable && (
          <div style={sectionStyle}>
            <h3 style={{ color: '#212529', marginBottom: '15px' }}>Введите значения X и Y:</h3>
            <div style={{ overflowX: 'auto', border: '1px solid #dee2e6', borderRadius: '4px' }}>
              <table style={{
                width: '100%',
                borderCollapse: 'collapse',
                minWidth: '300px'
              }}>
                <thead>
                  <tr style={{ backgroundColor: '#f8f9fa', borderBottom: '2px solid #dee2e6' }}>
                    <th style={{
                      padding: '12px',
                      textAlign: 'left',
                      color: '#495057',
                      fontWeight: '600',
                      width: '50px',
                      minWidth: '50px'
                    }}>
                      #
                    </th>
                    <th style={{
                      padding: '12px',
                      textAlign: 'left',
                      color: '#495057',
                      fontWeight: '600',
                      width: '50%',
                      minWidth: '150px'
                    }}>
                      X
                    </th>
                    <th style={{
                      padding: '12px',
                      textAlign: 'left',
                      color: '#495057',
                      fontWeight: '600',
                      width: '50%',
                      minWidth: '150px'
                    }}>
                      Y
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {points.map((point, index) => (
                    <tr key={index} style={{ borderBottom: '1px solid #dee2e6' }}>
                      <td style={{
                        padding: '12px',
                        color: '#6c757d',
                        fontWeight: '500',
                        width: '50px',
                        minWidth: '50px',
                        textAlign: 'center'
                      }}>
                        {index + 1}
                      </td>
                      <td style={{ padding: '8px', width: '50%', minWidth: '150px' }}>
                        <input
                          type="number"
                          value={point.x}
                          onChange={(e) => handleUpdatePoint(index, 'x', e.target.value)}
                          placeholder="Значение X"
                          step="any"
                          disabled={loading}
                          style={{
                            width: '100%',
                            padding: '8px 10px',
                            border: '1px solid #dee2e6',
                            borderRadius: '4px',
                            backgroundColor: '#f8f9fa',
                            color: '#212529',
                            fontSize: '14px',
                            boxSizing: 'border-box'
                          }}
                        />
                      </td>
                      <td style={{ padding: '8px', width: '50%', minWidth: '150px' }}>
                        <input
                          type="number"
                          value={point.y}
                          onChange={(e) => handleUpdatePoint(index, 'y', e.target.value)}
                          placeholder="Значение Y"
                          step="any"
                          disabled={loading}
                          style={{
                            width: '100%',
                            padding: '8px 10px',
                            border: '1px solid #dee2e6',
                            borderRadius: '4px',
                            backgroundColor: '#f8f9fa',
                            color: '#212529',
                            fontSize: '14px',
                            boxSizing: 'border-box'
                          }}
                        />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div style={{
              backgroundColor: '#fff3cd',
              border: '1px solid #ffeeba',
              color: '#856404',
              padding: '12px',
              borderRadius: '4px',
              marginTop: '15px',
              fontSize: '13px'
            }}>
              ⚠️ Значения X должны быть в возрастающем порядке!
            </div>
          </div>
        )}

        {error && <div style={errorStyle}>{error}</div>}
        {success && <div style={successStyle}>{success}</div>}

        <div style={{ display: 'flex', gap: '10px', marginTop: '20px', justifyContent: 'flex-end' }}>
          <button
            onClick={onClose}
            disabled={loading}
            style={{ ...buttonStyle, backgroundColor: '#6c757d', opacity: loading ? 0.6 : 1 }}
          >
            Отмена
          </button>
          {showTable && (
            <button
              onClick={handleCreateFunction}
              disabled={loading || points.length === 0 || !functionName.trim()}
              style={{ ...buttonStyle, backgroundColor: '#28a745', opacity: (loading || points.length === 0 || !functionName.trim()) ? 0.6 : 1 }}
            >
              {loading ? 'Создание...' : 'Создать функцию'}
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
