/**
 * @author Trần Quốc Thái
 * Class: SE1910
 * Student ID: CE190243
 */
import axios from "axios";

const axiosClient = axios.create({
  baseURL: "/api",
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 10000,
});

export default axiosClient;
