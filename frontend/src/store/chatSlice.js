import { createSlice } from "@reduxjs/toolkit";

const chatSlice = createSlice({
    name: "chat",
    initialState: {
        conversations: {}
    },
    reducers: {
        addOutgoingMessage: (state, action) => {
            const { contactId, text, timestamp } = action.payload;
            if (!contactId) {
                return;
            }
            if (!state.conversations[contactId]) {
                state.conversations[contactId] = [];
            }
            state.conversations[contactId].push({
                text,
                timestamp,
                isClient: true
            });
        },
        addIncomingMessage: (state, action) => {
            const { contactId, text, timestamp } = action.payload;
            if (!contactId) {
                return;
            }
            if (!state.conversations[contactId]) {
                state.conversations[contactId] = [];
            }
            state.conversations[contactId].push({
                text,
                timestamp,
                isClient: false
            });
        }
    }
});

export const { addOutgoingMessage, addIncomingMessage } = chatSlice.actions;

export default chatSlice.reducer;

