import { createSlice } from '@reduxjs/toolkit';

const THEME_KEY = 'erpms.theme';

const initialTheme = () => {
  const stored = localStorage.getItem(THEME_KEY);
  if (stored) return stored;
  return window.matchMedia?.('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
};

const themeSlice = createSlice({
  name: 'theme',
  initialState: { mode: initialTheme() },
  reducers: {
    setTheme(state, action) {
      state.mode = action.payload;
      localStorage.setItem(THEME_KEY, action.payload);
    },
    toggleTheme(state) {
      state.mode = state.mode === 'dark' ? 'light' : 'dark';
      localStorage.setItem(THEME_KEY, state.mode);
    },
  },
});

export const { setTheme, toggleTheme } = themeSlice.actions;
export default themeSlice.reducer;
