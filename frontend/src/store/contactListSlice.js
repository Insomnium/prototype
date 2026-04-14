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
  async (userId, { rejectedWithValue }) => {
    try {
      console.log(`Fetching contacts for: ${userId}`)
      const contacts = await contactListApi.getContacts(userId);
      const profiles = await profileApi.getProfiles(contacts.data.map(c => c.contactId));
      const aggregates = mergeAggregate(contacts, profiles);
      console.dir(aggregates);
      return aggregates;
    } catch (e) {
        rejectedWithValue(e.response?.data || "Failed to load contact aggregates");
    }
  }
);

const mergeAggregate = (contacts, profiles) => {
  const profilesById = profiles.data.results.reduce((profile, p) => {
    profile[p.id] = p; // Key by 'id' property
    return profile;
  }, {});

  return contacts.data.map(c => {
    const profile = profilesById[c.contactId]
    return {
        ...c,
        avatar: profile?.avatar,
        name: profile?.title,
        status: 'online'
    }
  });
};


const contactListSlice = createSlice({
  name: "contacts",
  initialState: {
    contacts: [],
    error: null,
    searchTerm: null,
    messagesByContact: { 216: [ { text: 'hi there', timestamp: new Date().getTime(), isClient: true } ] },
    selectedContact: null
  },
  reducers: {
      setSearchTerm: (state, action) => {
          state.searchTerm = action.payload;
      },
      setSelectedContact: (state, action) => {
          const contact = action.payload
          const contactId = contact.contactId;
          state.selectedContact = contact;
          console.log(">>> set selected contact: ")
          console.dir(contact)
          const contactMessages = state.messagesByContact[contactId]
          if (!contactMessages) {
              const debug = { ...state.messagesByContact, [contactId]: [] }
              state.messagesByContact = debug
          }
      },
      sendMessage: (state, action) => {
          // state.messagesByContact = { ...state.messagesByContact,  }
          state.messagesByContact[state.selectedContact.contactId].push(action.payload)
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
export const { setSearchTerm, setSelectedContact, sendMessage } = contactListSlice.actions;

// export selectors (selectors get part of the store state)
export const getAllContacts = (state) => state.contacts.contacts;
export const selectSearchTerm = (state) => state.searchTerm;
export const getSelectedContact = (state) => state.contacts.selectedContact;
export const getSelectedContactMessages = (state) => {
    const contacts = state.contacts;
    return (contacts.selectedContact && contacts.selectedContact && contacts.messagesByContact[contacts.selectedContact.contactId]) || [];
}

export default contactListSlice.reducer;
