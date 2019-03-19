import axios from "axios";
import appConfig from "./../../AppConfig";

export default {
  login: (username, password) => axios.post(`${appConfig.BASE_API_URL}/security/login`, {"name": username, "password": password})
};