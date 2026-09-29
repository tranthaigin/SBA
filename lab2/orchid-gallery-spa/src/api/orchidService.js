// src/api/orchidService.js – FETCH + CACHE
let orchidCache = null;
let cacheTime = 0;
const CACHE_DURATION = 30_000; // 30 seconds TTL

async function fetchOrchids() {
  const response = await fetch('/orchids.json', {
    headers: { Accept: 'application/json' }
  });
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}: Không thể tải orchids.json`);
  }
  return response.json();
}

const orchidService = {
  async getOrchids({ force = false } = {}) {
    const now = Date.now();
    const validCache = orchidCache && (now - cacheTime < CACHE_DURATION);

    if (!force && validCache) {
      console.log(`[orchidService] Cache hit! Returning cached data (${Math.round((now - cacheTime) / 1000)}s old)`);
      return orchidCache;
    }

    console.log(`[orchidService] Cache miss or force reload (force=${force}). Fetching from /orchids.json...`);
    const data = await fetchOrchids();
    orchidCache = data;
    cacheTime = now;
    return data;
  },

  async getOrchidById(id) {
    const list = await this.getOrchids();
    const orchid = list.find((item) => item.id === String(id));
    if (!orchid) throw new Error('Không tìm thấy Orchid');
    return orchid;
  },

  clearCache() {
    orchidCache = null;
    cacheTime = 0;
    console.log('[orchidService] Cache cleared');
  }
};

export default orchidService;
