import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Header.css';
import { useSelector } from 'react-redux';
import { useDispatch } from 'react-redux';
import { RootState } from './redux';
import { setPermission, setToken, setUsername, setUserNum } from './redux/ProfileSlice';
import axios from 'axios';

interface UserInfo {
  username: string;
  userNum: number;
  permission: number;
}

const Header: React.FC = () => {

  const navigate = useNavigate();
  const dispatch = useDispatch();

  const [isLogin, setIsLogin] = useState(false);
  const [ismaster, setIsmaster] = useState(false);

  const token = useSelector((state: RootState) => state.auth.token);
  const username = useSelector((state: RootState) => state.auth.username);
  const permission = useSelector((state: RootState) => state.auth.permission);


  const loginCheck = async () => {
    const localToken = localStorage.getItem("token");
    if (localToken) {
      dispatch(setToken(localToken));
      try {
        console.log(localToken);
        const response = await axios.post(
          "http://localhost:8891/back/api/user/getUsername",
          {},
          {
            headers: {
              Authorization: `Bearer ${localToken}`,
            },
          }
        );
        console.log(response.data);

        const resp: UserInfo = response.data;
        dispatch(setUsername(resp.username));
        dispatch(setUserNum(resp.userNum));
        dispatch(setPermission(resp.permission));
        setIsLogin(true);

      } catch (error) {
        console.error("로그인 확인 중 오류 발생:", error);
      }
    } else {
      setIsLogin(false);
    }
  };

  const Admin = () => {
    if (permission === 1) {
      console.log("유저, USER")
    } else if (permission === 63) {
      console.log("기자, EDITOR")
    } else if (permission === 127) {
      setIsmaster(true);
      console.log("관리자, MANAGER")
    } else {
      console.log("권한 없음, UNKNOWN")
    }
  }

  const handleLogout = () => {
    localStorage.removeItem("token");
    setIsmaster(false);
    setIsLogin(false);
    navigate("/");
    window.location.reload();
  };

  useEffect(() => {
    Admin();
  }, [permission])

  useEffect(() => {
    loginCheck();
  }, [token]);

  return (
    <div className="headercontroller">
      <header className="headercontroller-header">
        <nav className="headercontroller-nav">
          <div className="headercontroller-top">
            <ul className="headercontroller-top-list">
              <li className="headercontroller-logo">
                <Link to="/" id='headercontroller-title'>
                  <img src="/images/카툰바라.png" alt="카툰바라 로고" className="headercontroller-logo-img-main" /><div id='headercontroller-title-text'>카툰바라</div>
                </Link>
              </li>
              <div className="headercontroller-menu">
                <li className="headercontroller-item">
                  <Link to="/webtoon/list/1">웹툰리뷰</Link>
                </li>
                <li className="headercontroller-item">
                  <Link to="/cumm/Cummu_List/:page">커뮤니티</Link>
                </li>
                <li className="headercontroller-item">
                  <Link to="/user/Notice_List">공지사항</Link>
                </li>
                {!ismaster ? (
                  <>
                  </>
                ) : (
                  <li className="headercontroller-item">
                    <Link to="/manager/ManagerPage">홈페이지 관리</Link>
                  </li>
                )}

              </div>
              <div className="headercontroller-bottom">
                {!isLogin ? (
                  <div className="headercontroller-auth-links">
                    <Link to="/login/Login_Main" className="headercontroller-btn-click">로그인</Link>
                    <Link to="/login/Signup" className="headercontroller-btn-click">회원가입</Link>
                  </div>
                ) : (
                  <div className="headercontroller-user-info">
                    <button type="button" className="headercontroller-user" onClick={() => navigate("/mypage/Mypage_Main")}>
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="headercontroller-login-item" viewBox="0 0 16 16">
                        <path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0"></path>
                        <path fill-rule="evenodd" d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8m8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1"></path>
                      </svg>
                      {username}님
                    </button>
                    <button className="headercontroller-btn-click" onClick={handleLogout}>로그아웃</button>
                  </div>
                )}
              </div>
            </ul>
          </div>
        </nav >
      </header >
    </div >
  );
};

export default Header;