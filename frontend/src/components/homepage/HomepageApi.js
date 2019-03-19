import axios from "axios";
import appConfig from "./../../AppConfig";

export default {
  getUsersForDropdown: () => axios.get(`${appConfig.BASE_API_URL}/user/dropdown`),
};