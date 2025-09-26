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
        { id: 'user2', lastMessage: 'See you later', time: '2025-09-24', status: 'Online' },
        { id: 'user3', lastMessage: 'Okay', time: '2025-09-23', status: 'Online' },
        { id: 'user4', lastMessage: 'Fine', time: '2025-09-26', status: 'Offline' },
        { id: 'user5', lastMessage: 'KK', time: '2025-09-26', status: 'Online' },
    ]
};

export const profileApi = {
  getProfiles: async (ids) => [
        { id: 'user2', avatar: 'UT', name: 'User Two' },
        { id: 'user3', avatar: 'UT', name: 'User Three' },
        { id: 'user4', avatar: 'UF', name: 'User Four' },
        { id: 'user5', avatar: 'UF', name: 'User Five' },
      ]
};