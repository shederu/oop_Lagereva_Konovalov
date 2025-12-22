/*import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import CreateFromArray from './CreateFromArray';
import CreateFromFunction from './CreateFromFunction';
import Operations from './Operations';

export default function MainPage() {
  const [view, setView] = useState('home');
  const [functionsList, setFunctionsList] = useState([]);

  const navStyle = {
    display: 'flex',
    gap: '10px',
    padding: '20px',
    backgroundColor: '#f8f9fa',
    borderBottom: '1px solid #dee2e6'
  };

  const buttonStyle = (active) => ({
    padding: '10px 20px',
    backgroundColor: active ? '#007bff' : '#6c757d',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer'
  });

  // загрузка списка функций при входе на страницу
  useEffect(() => {
    handleRefresh();
  }, []);

  const handleRefresh = async () => {
    try {
      const data = await api.getFunctions();
      setFunctionsList(data);
    } catch (e) {
      console.error('Ошибка загрузки функций', e);
    }
  };

  return (
    <div>
      <nav style={navStyle}>
        <button
          style={buttonStyle(view === 'home')}
          onClick={() => setView('home')}
        >
          Главная
        </button>
        <button
          style={buttonStyle(view === 'create-array')}
          onClick={() => setView('create-array')}
        >
          Создать из массива
        </button>
        <button
          style={buttonStyle(view === 'create-function')}
          onClick={() => setView('create-function')}
        >
          Создать из функции
        </button>
        <button
          style={buttonStyle(view === 'operations')}
          onClick={() => setView('operations')}
        >
          Операции
        </button>
      </nav>

      <div style={{ padding: '20px' }}>
        {view === 'home' && (
          <p>Выберите операцию из меню выше.</p>
        )}

        {view === 'create-array' && (
          <CreateFromArray
            onClose={() => setView('home')}
            onSuccess={handleRefresh}
            existingFunctions={functionsList}
          />
        )}

        {view === 'create-function' && (
          <CreateFromFunction
            onClose={() => setView('home')}
            onSuccess={handleRefresh}
            existingFunctions={functionsList}
          />
        )}

        {view === 'operations' && (
          <Operations
            onClose={() => setView('home')}
            onChanged={handleRefresh}
            existingFunctions={functionsList}
          />
        )}
      </div>
    </div>
  );
}
*/
import React, { useState } from 'react';
import CreateFromArray from './CreateFromArray';
import CreateFromFunction from './CreateFromFunction';
import Operations from './Operations';

export default function MainPage() {
  const [showCreateFromArray, setShowCreateFromArray] = useState(false);
  const [showCreateFromFunction, setShowCreateFromFunction] = useState(false);
  const [showOperations, setShowOperations] = useState(false);

  const navStyle = {
    display: 'flex',
    gap: '10px',
    padding: '15px 20px',
    backgroundColor: '#f8f9fa',
    borderBottom: '1px solid #dee2e6',
    flexWrap: 'wrap'
  };

  const buttonStyle = {
    padding: '10px 16px',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontWeight: '500',
    fontSize: '14px'
  };

  const contentStyle = {
    padding: '40px 20px',
    textAlign: 'center',
    color: '#6c757d',
    minHeight: '60vh'
  };

  return (
    <div style={{ backgroundColor: '#ffffff' }}>
      <nav style={navStyle}>
        <button
          onClick={() => setShowCreateFromArray(true)}
          style={{...buttonStyle}}
        >
          📊 Создать из массива
        </button>
        <button
          onClick={() => setShowCreateFromFunction(true)}
          style={{...buttonStyle}}
        >
          📈 Создать из функции
        </button>
        <button
          onClick={() => setShowOperations(true)}
          style={{...buttonStyle}}
        >
          ➕ Операции
        </button>
      </nav>

      <div style={contentStyle}>
        <h1 style={{ color: '#212529', marginTop: 0 }}>Function Manager</h1>
        <p>Выберите операцию из меню выше</p>
      </div>

      {showCreateFromArray && (
        <CreateFromArray
          onClose={() => setShowCreateFromArray(false)}
          onSuccess={(data) => console.log('Функция создана:', data)}
        />
      )}

      {showCreateFromFunction && (
        <CreateFromFunction
          onClose={() => setShowCreateFromFunction(false)}
          onSuccess={(data) => console.log('Функция создана:', data)}
        />
      )}

      {showOperations && (
        <Operations
          onClose={() => setShowOperations(false)}
        />
      )}
    </div>
  );
}
