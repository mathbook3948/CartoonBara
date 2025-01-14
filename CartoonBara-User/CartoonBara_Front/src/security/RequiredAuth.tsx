import React, { ReactNode, useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

interface Props {
  children: ReactNode;
}

const RequiredAuth: React.FC<Props> = ({ children }) => {
  const [isLogined, setIsLogined] = useState(false);
  const token = localStorage.getItem('token');
  const navigate = useNavigate();

  useEffect(() => {
    login();
  }, [token, navigate]);

  const login = () => {
    if (token === null || token === "") {
      const timer = setTimeout(() => {
        navigate('/login/Login_Main');
      }, 0);
      setIsLogined(false);
      return () => clearTimeout(timer);
    } else {
      setIsLogined(true);
      return 
    }
  }


  return isLogined ? <>{children}</> : <></>;
};

export default RequiredAuth;
