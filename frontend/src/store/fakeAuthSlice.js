import { createSlice } from "@reduxjs/toolkit";

const fakeAuthSlice = createSlice({
    name: 'fakeAuth',
    initialState: {
        fakeUserId: '-1'
    },
    reducers: {
        setUserId: (state, action) => {
            state.fakeUserId = action.payload;
        }
    }
});

export const { setUserId } = fakeAuthSlice.actions;

export const getFakeUserId = (state) => state.fakeAuth.fakeUserId;

export default fakeAuthSlice.reducer;
