import axios from 'axios';
import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom';
import './SurveyList.css';

interface SurveyContent {
  surveytype: string;
  surveytitle: string;
  surveycnt: number;
}

interface Survey {
  num:number;
  sub: string;
  code: number;
  contents: SurveyContent[];
}


const SurveyList: React.FC = () => {

  const [survey, setSurvey] = useState<Survey[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
     const fetchAllSurvey = async () => {
    try {
      const response = await axios.get(`http://localhost:8892/back/api/survey/allList`);
      if (response.status === 200) {
        console.log(response.data);
        setSurvey(response.data);
      } else {
        console.log("No survey data available.");
      }
    } catch (error) {
      console.error("Failed to fetch survey:", error);
    }
  };
    fetchAllSurvey();
  },[])
  
  return (
    <div className="container2">
      <button type="button" className="survey-button" onClick={() => navigate("/manager/survey/surveyadd")}>
                  설문 등록
      </button>
      <button type="button" className="survey-button" onClick={() => navigate("/survey")}>
                  처음 화면 (진행중인 설문)
      </button>
      <button type="button" className="survey-button" onClick={() => navigate("/manager/survey/back/surveyResult")}>
                  투표 결과
      </button>
    <h1>설문 리스트</h1>
    <table className="survey-table">
      <thead>
        <tr>
          <th>번호</th>
          <th>설문 제목</th>
          <th>설문 내용</th>
        </tr>
      </thead>
      <tbody className="survey-tbody">
        {survey.map((item) => (
          <tr key={item.num}>
            <td>{item.num}</td>
            <td>{item.sub}</td>
            <td>
              <table style={{ borderCollapse: "collapse", width: "100%" }}>
                <thead>
                  <tr>
                    <th>설문 제목</th>
                    <th>설문 수</th>
                  </tr>
                </thead>
                <tbody>
                  {item.contents.map((content, index) => (
                    <tr key={index}>
                      <td>{content.surveytitle}</td>
                      <td>{content.surveycnt}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  </div>
  )
}

export default SurveyList