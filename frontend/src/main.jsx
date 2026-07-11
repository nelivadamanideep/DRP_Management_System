import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { Provider } from 'react-redux';
import { Toaster } from 'react-hot-toast';

import './index.css';
import { store } from './app/store';
import { App } from './app/App';
import { ThemeBoot } from './app/ThemeBoot';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <Provider store={store}>
      <BrowserRouter>
        <ThemeBoot />
        <App />
        <Toaster
          position="top-right"
          toastOptions={{
            className: 'font-sans text-sm',
            style: {
              background: '#131a31',
              color: '#f6f7fb',
              borderRadius: '14px',
              boxShadow: '0 10px 30px rgba(15,23,42,0.35)',
            },
          }}
        />
      </BrowserRouter>
    </Provider>
  </React.StrictMode>
);
