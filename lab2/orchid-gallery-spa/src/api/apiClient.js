// src/api/apiClient.js
import axios from 'axios';

export const apiClient = axios.create({
  timeout: 5000,
  headers: { Accept: 'application/json' }
});

export default apiClient;
