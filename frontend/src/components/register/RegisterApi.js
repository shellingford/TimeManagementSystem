import axios from "axios";
import appConfig from "./../../AppConfig";

export default {
  register: (username, password, confirmPassword) => axios.post(`${appConfig.BASE_API_URL}/security/register`,
    {"name":username, "password":password, "confirmPassword":confirmPassword}),
};