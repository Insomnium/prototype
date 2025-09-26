import { configureStore } from "@reduxjs/toolkit";
import { default as contactListReducer } from './contactListSlice'

const store = configureStore({
    reducer: {
        contacts: contactListReducer // this `contacts` is the key under which data will be kept in the store. E.g. state.contacts.contacts - where first `contacts` is a reducer key here, second `contacts` is from slice's initialState/extraReducers
    }
});

export default store;