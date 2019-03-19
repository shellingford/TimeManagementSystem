import axios from "axios";
import appConfig from "./../../AppConfig";

export default {
  getAll: (params) => axios.get(`${appConfig.BASE_API_URL}/timeentry/`, { params }),
  update: (id, data) => {
        if (id === -1) { //new entry
            return axios.post(`${appConfig.BASE_API_URL}/timeentry/`, data)
        } else {
            return axios.put(`${appConfig.BASE_API_URL}/timeentry/${id}`, data)
        } 
    },
  delete: (id) => axios.delete(`${appConfig.BASE_API_URL}/timeentry/${id}`),
};