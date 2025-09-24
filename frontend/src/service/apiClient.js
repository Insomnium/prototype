import axios from "axios";

const chatApiClient = axios.create({
  baseUrl: "http://localhost:8082/api",
  timeout: 5000,
  headers: {
    "Content-Type": "application/json",
  },
});

export const chatApi = {
    getContacts: async () => {
        const response = await chatApiClient.get('/v1/contacts')
    }
}