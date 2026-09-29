// src/api/orchidService.axios.example.js
import { apiClient } from './apiClient';

/**
 * Phiên bản service dùng Axios để so sánh với Fetch API
 * - Axios tự động parse JSON (response.data)
 * - Tự động reject Promise khi status code nằm ngoài dải 2xx
 */
export async function getOrchidsByAxios() {
  const response = await apiClient.get('/orchids.json');
  return response.data;
}

export default {
  getOrchidsByAxios
};
