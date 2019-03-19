import axios from "axios";
import appConfig from "./../../AppConfig";

export default {
  logout: () => axios.post(`${appConfig.BASE_API_URL}/security/logout`)
};