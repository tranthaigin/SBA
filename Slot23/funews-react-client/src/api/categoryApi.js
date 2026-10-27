import api from "./axiosClient";

export function getCategories({
  keyword = "",
  page = 0,
  size = 10,
  sort = "categoryName,asc",
  signal,
} = {}) {
  return api.get("/categories", {
    params: {
      keyword: keyword || undefined,
      page,
      size,
      sort,
    },
    signal,
  });
}

export function getCategoryById(id, { signal } = {}) {
  return api.get(`/categories/${id}`, { signal });
}

export function createCategory(payload) {
  return api.post("/categories", payload);
}

export function updateCategory(id, payload) {
  return api.put(`/categories/${id}`, payload);
}

export function deleteCategory(id) {
  return api.delete(`/categories/${id}`);
}
