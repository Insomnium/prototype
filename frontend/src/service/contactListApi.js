import axios from "axios";

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
    // getContacts: () => chatApiClient.get('/contacts')
    getContacts: async () => [
        { id: 'user2', lastMessage: 'See you later', time: '2025-09-24' },
        { id: 'user3', lastMessage: 'Okay', time: '2025-09-23' }
    ]
};

export const profileApi = {
  getProfiles: async (ids) => [
        { id: 'user2', avatar: 'UT', name: 'User Two' },
        { id: 'user3', avatar: 'UT', name: 'User Three' }
      ]
};