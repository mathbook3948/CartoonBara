import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface Profile {
    username: string;
    userNum: number;
    token: string;
    permission: number;
}

const initialState: Profile = {
    username: "",
    userNum: -1,
    token: "",
    permission: -1
}

const profileSlice = createSlice({
    name: "profile",
    initialState,
    reducers: {
        setUsername: (state, action: PayloadAction<string>) => {
            state.username = action.payload;
        },
        setToken: (state, action: PayloadAction<string>) => {
            state.token = action.payload
        },
        setUserNum: (state, action: PayloadAction<number>) => {
            state.userNum = action.payload;
        },
        setPermission: (state, action: PayloadAction<number>) => {
            state.permission = action.payload;
        }
    }
})

export const { setUsername, setToken, setUserNum, setPermission } = profileSlice.actions;
export default profileSlice.reducer;