import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { contactListApi } from "../service/contactListApi";

export const fetchContacts = createAsyncThunk(
  "fetchContacts", // key doesn't matter here
  async (_, { rejectedWithValue }) => {
    try {
      const response = await contactListApi.getContacts();
      return response;
    } catch (e) {
      rejectedWithValue(e.response?.data || "Failed to load contacts");
    }
  }
);

const contactListSlice = createSlice({
  name: "contacts",
  initialState: {
    contacts: [],
    error: null,
    searchTerm: null,
  },
  reducers: {
    setSearchTerm: (state, action) => {
      state.searchTerm = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
        .addCase(fetchContacts.pending, (state) => {
            state.loading = true,
            state.error = null
        })
        .addCase(fetchContacts.fulfilled, (state, action) => {
            state.loading = false,
            state.contacts = action.payload
        })
        .addCase(fetchContacts.rejected, (state, action) => {
            state.loading = false
            state.error = action.payload
        })
  }
});

// export actions. Actions are matched with reducers
export const { setSearchTerm } = contactListSlice.actions;

// export selectors (selectors get part of the store state)
export const getAllContacts = (state) => state.contacts.contacts;
export const selectSearchTerm = (state) => state.searchTerm;

export default contactListSlice.reducer;
