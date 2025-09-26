import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { contactListApi, profileApi } from "../service/contactListApi";

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

export const fetchContactAggregates = createAsyncThunk(
  "fetchContactAggregates",
  async (_, { rejectedWithValue }) => {
    try {
      const contacts = await contactListApi.getContacts();
      const profiles = await profileApi.getProfiles(contacts.map(c => c.id));
      const aggregates = mergeAggregate(contacts, profiles);
      return aggregates;
    } catch (e) {
        rejectedWithValue(e.response?.data || "Failed to load contact aggregates");
    }
  }
);

const mergeAggregate = (contacts, profiles) => {
  const profilesById = profiles.reduce((profile, p) => {
    profile[p.id] = p; // Key by 'id' property
    return profile;
  }, {});

  return contacts.map(c => {
    const profile = profilesById[c.id]
    return {
        ...c,
        avatar: profile?.avatar,
        name: profile?.name
    }
  });
};


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
    setSelectedContact: (state, action) => {
      state.selectedContact = action.payload;
    }
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
        .addCase(fetchContactAggregates.pending, (state) => {
            state.loading = true,
            state.error = null
        })
        .addCase(fetchContactAggregates.fulfilled, (state, action) => {
            state.loading = false,
            state.contacts = action.payload
        })
        .addCase(fetchContactAggregates.rejected, (state, action) => {
            state.loading = false
            state.error = action.payload
        })
  }
});

// export actions. Actions are matched with reducers
export const { setSearchTerm, setSelectedContact } = contactListSlice.actions;

// export selectors (selectors get part of the store state)
export const getAllContacts = (state) => state.contacts.contacts;
export const selectSearchTerm = (state) => state.searchTerm;
export const getSelectedContact = (state) => state.contacts.selectedContact;

export default contactListSlice.reducer;
