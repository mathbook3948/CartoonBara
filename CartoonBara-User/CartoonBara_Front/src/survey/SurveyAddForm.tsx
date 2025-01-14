import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import './SurveyAddForm.css';
import RequiredAuth from "../security/RequiredAuth";
import { RootState } from "../redux";
import { useSelector } from "react-redux";



const SurveyAddForm: React.FC = () => {
  const [sub, setSub] = useState("");
  const [code, setCode] = useState("2"); // 기본값 2로 설정
  const [surveyTitles, setSurveyTitles] = useState<string[]>(Array(2).fill(""));
  // numValue 크기의 배열을 생성, 모든 요소를 빈 문자열("")로 채움

  const permission = useSelector((state: RootState) => state.auth.permission);

  const navigate = useNavigate();

  const handleCodeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = e.target.value; // 항목수가 추가되면 문항수를 불러오는 함수
    const numValue = parseInt(newValue);

    // 항목 수 만큼 빈 공간을 만드는 함수
    if (numValue >= 2 && numValue <= 5) {
      setCode(newValue);
      // prev => {} : 상태를 업데이트할 때 이전 상태(prev) 값을 기반으로 새 상태를 계산하는 함수형 업데이트 방식
      setSurveyTitles(prev => {
        const newArray = Array(numValue).fill("");
        // 이전 상태 배열(prev)에서 동일한 인덱스에 값이 있다면(prev[index]) 그 값을 유지
        // 이전 상태 배열에 값이 없다면 빈 문자열("")을 사용
        return newArray.map((item, index) => prev[index] || "");
      });
    }
  };
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const surveyData = {  // 다음과 같은 형식으로 데이터가 들어가야 함
        sub,                                    // private String sub;
        code: parseInt(code),                   // private Integer code;
        contents: surveyTitles.map(title => ({  // private List<SurveyContentVO> contents;
          surveytitle: title,
        }))
      };

      const response = await axios.post(
        `http://localhost:8892/back/api/survey/addsurvey`,
        surveyData
      );

      if (response.status === 200) {
        alert("설문이 등록되었습니다.");
        navigate("/survey");
      }
    } catch (error) {
      console.error("Error submitting survey:", error);
      alert("설문 등록에 실패했습니다.");
    }
  };


  const handleTitleChange = (index: number, value: string) => {
    const newTitles = [...surveyTitles];
    newTitles[index] = value;
    setSurveyTitles(newTitles);
  };

  return (
    <RequiredAuth>
      {permission === 127 ? (
        <>
          <div className="survey-container">
            <form onSubmit={handleSubmit} className="survey-form">
              <table className="survey-form-table">
                <thead>
                  <tr><th colSpan={2} className="survey-table-header">설문조사 작성 폼</th></tr>
                </thead>
                <tbody className="survey-form-tbody">
                  <tr>
                    <th className="survey-table-th">제목</th>
                    <td className="survey-table-td">
                      <input
                        type="text"
                        value={sub}
                        onChange={(e) => setSub(e.target.value)}
                        required
                        className="input-field"
                      />
                    </td>
                  </tr>
                  <tr>
                    <th className="survey-table-th">문항수 (2-5)</th>
                    <td className="survey-table-td">
                      <input
                        type="number"
                        min="2"
                        max="5"
                        value={code}
                        onChange={handleCodeChange}
                        required
                        className="input-field"
                      />
                    </td>
                  </tr>
                  {surveyTitles.map((title, index) => (
                    <tr key={index}>
                      <th className="survey-table-th">설문문항{index + 1}</th>
                      <td className="survey-table-td">
                        <input
                          type="text"
                          value={title}
                          onChange={(e) => handleTitleChange(index, e.target.value)}
                          required
                          className="input-field"
                        />
                      </td>
                    </tr>
                  ))}
                </tbody>
                <tfoot>
                  <tr>
                    <th colSpan={2} className="survey-table-footer">
                      <button className="survey-action-button" type="submit">등록</button>
                      <button className="survey-action-button" type="button" onClick={() => navigate("/manager/survey/surveylist")}>
                        목록
                      </button>
                    </th>
                  </tr>
                </tfoot>
              </table>
            </form>
          </div>
        </>
      ) : (
        <>
          <div style={{
            padding: '20px',
            border: '1px solid #ff0000',
            borderRadius: '8px',
            backgroundColor: '#ffcccc',
            color: '#ff0000',
            fontSize: '18px',
            textAlign: 'center',
          }}>
            접근할 수 없는 권한입니다.
          </div>
        </>
      )}

    </RequiredAuth>
  )
};

export default SurveyAddForm;