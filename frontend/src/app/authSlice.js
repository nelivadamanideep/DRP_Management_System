import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { api, tokenStore } from './apiClient';

const CURRENT_USER_KEY = 'erpms.currentUser';

const readStoredUser = () => {
  try {
    const raw = localStorage.getItem(CURRENT_USER_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
};

export const login = createAsyncThunk('auth/login', async ({ email, password }) => {
  const response = await api.post('/auth/login', { email, password });
  tokenStore.set(response.data.accessToken, response.data.refreshToken);
  const user = {
    userId: response.data.userId,
    email: response.data.email,
    fullName: response.data.fullName,
    role: response.data.role,
  };
  localStorage.setItem(CURRENT_USER_KEY, JSON.stringify(user));
  return user;
});

export const register = createAsyncThunk('auth/register', async (payload) => {
  const response = await api.post('/auth/register', payload);
  tokenStore.set(response.data.accessToken, response.data.refreshToken);
  const user = {
    userId: response.data.userId,
    email: response.data.email,
    fullName: response.data.fullName,
    role: response.data.role,
  };
  localStorage.setItem(CURRENT_USER_KEY, JSON.stringify(user));
  return user;
});

export const logout = createAsyncThunk('auth/logout', async () => {
  const refreshToken = tokenStore.refresh();
  if (refreshToken) {
    try { await api.post('/auth/logout', { refreshToken }); } catch { /* ignore */ }
  }
  tokenStore.clear();
  localStorage.removeItem(CURRENT_USER_KEY);
  return null;
});

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    user: readStoredUser(),
    status: 'idle',
    error: null,
  },
  reducers: {
    setUser(state, action) {
      state.user = action.payload;
      if (action.payload) localStorage.setItem(CURRENT_USER_KEY, JSON.stringify(action.payload));
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => { state.status = 'loading'; state.error = null; })
      .addCase(login.fulfilled, (state, action) => { state.status = 'idle'; state.user = action.payload; })
      .addCase(login.rejected, (state, action) => {
        state.status = 'idle';
        state.error = action.error.message;
      })
      .addCase(register.fulfilled, (state, action) => { state.user = action.payload; })
      .addCase(logout.fulfilled, (state) => { state.user = null; });
  },
});

export const { setUser } = authSlice.actions;
export default authSlice.reducer;
