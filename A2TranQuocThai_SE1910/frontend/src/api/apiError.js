export function getApiErrorMessage(error, fallback) {
  if (error.response?.data?.message) {
    return error.response.data.message;
  }

  if (error.code === "ERR_NETWORK") {
    return "Cannot reach the API. Check that Slot 22 is running, VITE_API_URL is correct, and CORS allows this origin.";
  }

  return error.message || fallback;
}
