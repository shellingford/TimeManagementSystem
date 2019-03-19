import axios from "axios";
import appConfig from "./../../AppConfig";

export default {
  get: (params) => axios.get(`${appConfig.BASE_API_URL}/usersetting/`, { params }),
  update: (id, data) => {
        if (id <= 0) { //new entry
            return axios.post(`${appConfig.BASE_API_URL}/usersetting/`, data)
        } else {
            return axios.put(`${appConfig.BASE_API_URL}/usersetting/${id}`, data)
        } 
    },
  delete: (id) => axios.delete(`${appConfig.BASE_API_URL}/usersetting/${id}`),
};