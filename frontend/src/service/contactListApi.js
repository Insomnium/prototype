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
        { id: 'user6', lastMessage: 'When?', time: '2025-09-26', status: 'Online' },
    ]
};

export const profileApi = {
  getProfiles: async (ids) => [
        { id: 'user2', avatar: 'LV', name: 'Liz Vicious' },
        { id: 'user3', avatar: 'MM', name: 'Melisa Mendiny' },
        { id: 'user4', avatar: 'DS', name: 'Desirae Spencer' },
        { id: 'user5', avatar: 'SF', name: 'Sweetie Fox' },
        { id: 'user6', avatar: 'AT', name: 'Alison Tyler' },
      ]
};