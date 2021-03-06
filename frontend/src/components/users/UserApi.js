import axios from "axios";
import appConfig from "./../../AppConfig";

export default {
  getAll: (params) => axios.get(`${appConfig.BASE_API_URL}/user/`, { params }),
  update: (id, data) => {
        if (id === -1) { //new entry
            return axios.post(`${appConfig.BASE_API_URL}/user/`, data)
        } else {
            return axios.put(`${appConfig.BASE_API_URL}/user/${id}`, data)
        } 
    },
  delete: (id) => axios.delete(`${appConfig.BASE_API_URL}/user/${id}`),
};