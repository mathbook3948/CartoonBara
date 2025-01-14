import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './surveyAlert.css';

interface ModalProps {
  className?: string;
  onClose: (e: React.MouseEvent | boolean) => void;
  maskClosable?: boolean;
  closeable?: boolean;
  visible?: boolean;
}

const SurveyAlert: React.FC<ModalProps> = ({
  className,
  onClose,
  maskClosable = true,
  closeable = true,

}) => {
  const [visible, setVisible] = useState(true);
  const navigate = useNavigate();


  useEffect(() => {
    const todayDate = new Date().toISOString().split('T')[0]; // 오늘 날짜를 문자열로 저장
    const storedDate = localStorage.getItem('surveyPopupClosedDate');

    if (storedDate === todayDate) {
      // 오늘 이미 "오늘 하루 보지 않기"를 클릭한 경우
      setVisible(false);
    }
  }, []);

  const handleNavigate = () => {
    navigate('/survey');
  };

  const handleDayClose = () => {
    const todayDate = new Date().toISOString().split('T')[0]; // 오늘 날짜 저장
    localStorage.setItem('surveyPopupClosedDate', todayDate);
    setVisible(false); // 팝업 숨기기
  };

  const onMaskClick = (e: React.MouseEvent) => {
    if (e.target === e.currentTarget && maskClosable) {
      onClose(e);
    }
  };

  return visible ? (
    <div className="surveyAlert-wrapper">
      <div className="sureyAlery-div">
        <img
          onClick={handleNavigate}
          src="/images/image.png"
          className="sureyAlery-div-image"
          alt="survey"
        />
        {closeable && (
          <div className='survey-alert-div'>
            <button className="survey-alert-close" onClick={handleDayClose}>
              오늘 하루 보지않기
            </button>
            <button className="survey-alert-close" onClick={() => setVisible(false)}>
              닫기
            </button>
          </div>
        )}
      </div>
    </div>
  ) : null;
}

export default SurveyAlert;
