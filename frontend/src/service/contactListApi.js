import axios from "axios";

const chatApiClient = axios.create({
  baseURL: "https://dummyjson.com",
  timeout: 5000,
  headers: {
    "Content-Type": "application/json",
  },
});

export const contactListApi = {
    // getContacts: () => chatApiClient.get('/users')
    getContacts: () => [
        { id: 'user2', avatar: 'UT', name: 'User Two', lastMessage: 'See you later', time: '2025-09-24' },
        { id: 'user3', avatar: 'UT', name: 'User Three', lastMessage: 'Okay', time: '2025-09-23' }
    ]
};