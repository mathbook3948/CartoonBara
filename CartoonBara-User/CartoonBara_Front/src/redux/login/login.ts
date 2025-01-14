import axios from "axios";
import { AppDispatch, RootState } from "../store";
import { setPermission, setToken, setUsername, setUserNum } from "../ProfileSlice";
import { useSelector } from "react-redux";

interface UserInfo {
    username: string;
    userNum : number;
    permission: number;
}

const login = async (username: string, password: string, dispatch: AppDispatch) => {
    try {
        const response = await axios.post("http://localhost:8891/back/api/user/login", { username, password });
        const token = response.data;
        dispatch(setToken(token));
        localStorage.setItem("token", token);

        // token이 있을 경우, username을 가져오는 API 호출
        const response2 = await axios.post(
            "http://localhost:8891/back/api/user/getUsername",
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            }
        );
            
            const userInfo: UserInfo = response2.data;
            dispatch(setUsername(userInfo.username));
            dispatch(setUserNum(userInfo.userNum));
            dispatch(setPermission(userInfo.permission));
    } catch (error) {
        console.log(error);
    }
};

export default login;

