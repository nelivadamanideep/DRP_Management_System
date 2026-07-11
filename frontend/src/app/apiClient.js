import axios from 'axios';
import toast from 'react-hot-toast';

// The Vite dev server proxies `/api` → backend (see vite.config.js).
// In production the same `/api` prefix is served by Nginx.
const baseURL = import.meta.env.VITE_BACKEND_URL
  ? `${import.meta.env.VITE_BACKEND_URL}/api`
  : '/api';

export const api = axios.create({
  baseURL,
  headers: { 'Content-Type': 'application/json' },
  timeout: 30000,
});

const STORAGE_KEY_ACCESS = 'erpms.accessToken';
const STORAGE_KEY_REFRESH = 'erpms.refreshToken';

export const tokenStore = {
  access: () => localStorage.getItem(STORAGE_KEY_ACCESS),
  refresh: () => localStorage.getItem(STORAGE_KEY_REFRESH),
  set(accessToken, refreshToken) {
    if (accessToken) localStorage.setItem(STORAGE_KEY_ACCESS, accessToken);
    if (refreshToken) localStorage.setItem(STORAGE_KEY_REFRESH, refreshToken);
  },
  clear() {
    localStorage.removeItem(STORAGE_KEY_ACCESS);
    localStorage.removeItem(STORAGE_KEY_REFRESH);
  },
};

api.interceptors.request.use((config) => {
  const token = tokenStore.access();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

let refreshInFlight = null;

async function attemptRefresh() {
  if (refreshInFlight) return refreshInFlight;
  const refreshToken = tokenStore.refresh();
  if (!refreshToken) return null;
  refreshInFlight = axios
    .post(`${baseURL}/auth/refresh`, { refreshToken })
    .then((res) => {
      tokenStore.set(res.data.accessToken, res.data.refreshToken);
      return res.data.accessToken;
    })
    .catch(() => {
      tokenStore.clear();
      return null;
    })
    .finally(() => {
      refreshInFlight = null;
    });
  return refreshInFlight;
}

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config;
    if (
      error.response?.status === 401 &&
      original &&
      !original._retry &&
      !original.url?.includes('/auth/')
    ) {
      original._retry = true;
      const newToken = await attemptRefresh();
      if (newToken) {
        original.headers.Authorization = `Bearer ${newToken}`;
        return api.request(original);
      }
      window.location.href = '/login';
    }
    const message =
      error.response?.data?.message ||
      error.response?.data?.error ||
      error.message ||
      'Something went wrong';
    if (error.response?.status !== 401 && !original?._silent) {
      toast.error(message);
    }
    return Promise.reject(error);
  }
);
