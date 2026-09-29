// src/hooks/useOrchids.js
import { useCallback, useEffect, useState } from 'react';
import orchidService from '../api/orchidService';

export default function useOrchids() {
  const [orchids, setOrchids] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const loadOrchids = useCallback(async (force = false) => {
    setLoading(true);
    setError(null);
    try {
      const data = await orchidService.getOrchids({ force });
      setOrchids(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Không thể tải danh sách Orchids');
    } finally {
      setLoading(false);
    }
  }, []);

  const reload = useCallback(() => loadOrchids(true), [loadOrchids]);

  useEffect(() => {
    loadOrchids(false);
  }, [loadOrchids]);

  return { orchids, loading, error, reload };
}
