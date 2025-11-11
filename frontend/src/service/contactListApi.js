import axios from "axios";
import QueryString from "qs";

const chatApiClient = axios.create({
  baseURL: import.meta.env.VITE_BE_CHAT_BASE_URL,
  timeout: 5000,
  headers: {
    "Content-Type": "application/json",
  },
});

const profileApiClient = axios.create({
  baseURL: import.meta.env.VITE_BE_CORE_BASE_URL,
  timeout: 5000,
  headers: {
    "Content-Type": "application/json",
  },
});

export const contactListApi = {
  getContacts: (userId) =>
    chatApiClient.get("/p2p/contacts", {
      headers: {
        "X-User-Id": userId,
      },
    })
};

export const profileApi = {
  getProfiles: async (profileIds) => 
    profileApiClient.get("/profiles/list", {
      params: {
        ids: profileIds
      },
      paramsSerializer: (params) => {
        return `ids=${params.ids.join(',')}`
      }
    }),
};
