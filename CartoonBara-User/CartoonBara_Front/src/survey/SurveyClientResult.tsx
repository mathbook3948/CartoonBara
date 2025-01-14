import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import './SurveyClientResult.css';

interface SurveyContent {
  surveytype: string;
  surveytitle: string;
  surveycnt: number;
}

interface Survey {
  num: number;
  sub: string;
  code: number;
  sdate: string;
  contents: SurveyContent[];
}

const SurveyClientResult: React.FC = () => {
  const [survey, setSurvey] = useState<Survey | null>(null);

  const navigate = useNavigate();
  useEffect(() => {
    const fetchLatestSurvey = async () => {
      try {
        const response = await axios.get(`http://localhost:8892/back/api/survey/latest`);
        if (response.status === 200) {
          setSurvey(response.data);
        }
      } catch (error) {
        console.error("Failed to fetch survey results:", error);
      }
    };

    fetchLatestSurvey();
  }, []);

  if (!survey) {
    return <div>결과를 불러오는 중...</div>;
  }

    const formatDate = (dateString: string) => {
      const date = new Date(dateString); // 문자열을 Date 객체로 변환
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0"); // 월 (0부터 시작하므로 +1 필요)
      const day = String(date.getDate()).padStart(2, "0"); // 일
  
      return `${year}-${month}-${day}`;
    };

  // 전체 투표 수 계산
  const totalVotes = survey.contents.reduce((sum, content) => sum + content.surveycnt, 0);

  return (
    <div className="container2">
      <h2>{survey.sub} - 투표 결과</h2>
      <button type="button" className="survey-button" onClick={() => navigate("/manager/survey/surveylist")}>
                  설문 목록
      </button>
      <button type="button" className="survey-button" onClick={() => navigate("/manager/survey/surveyadd")}>
                  설문 등록
      </button>
      <button type="button" className="survey-button" onClick={() => navigate("/manager/survey")}>
                  진행중인 설문
      </button>
      <p> 설문 등록 날짜 : {formatDate(survey.sdate)}</p>
      <div className="survey-result">
        {survey.contents.map((content, index) => {
          const percentage = totalVotes > 0 
            ? Math.round((content.surveycnt / totalVotes) * 100) 
            : 0;

          return (
            <div key={index} className="survey-result-item">
              <div className="survey-result-label">
                {content.surveytitle} ({content.surveycnt}표)
              </div>
              <div className="survey-progress-bar">
                <div 
                  className="survey-progress-fill" 
                  style={{ width: `${percentage}%` }}
                />
              </div>
              <div className="survey-percentage">{percentage}%</div>
            </div>
          );
        })}
      </div>
      <div className="survey-total-votes">
        총 투표 수: {totalVotes}
      </div>
    </div>
  );
};

export default SurveyClientResult;