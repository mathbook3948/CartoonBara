import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Header from './Header';
import Footer from './Footer';
import MyPage from './mypage-진헌/MyPageMain/MyPage';
import MyPageMenu from './mypage-진헌/MyPageMenu/MyPageMenu';
import ProfileEdit from './mypage-진헌/ProfileEdit/ProfileEdit';
import SavedPosts from './mypage-진헌/SavedPosts/SavedPosts';
import Home from './Home-채린/Home';
import WebtoonList from './webtoon-정우,윤호/WebtoonList';
import WebtoonDetail from './webtoon-정우,윤호/WebtoonDetail';
import WebtoonReview from './webtoon-정우,윤호/WebtoonReview';
import Login from './login-나은,정우, 진헌/Login';
import PasswordSearch from './login-나은,정우, 진헌/PasswordSearch';
import SecuritySettings from './mypage-진헌/SecuritySetting/SecuritySettings';
import FollowedUsers from './mypage-진헌/FollowedUsers/FollowedUsers';
import MySinGO from './mypage-진헌/Singo/MySinGO';
import MyComments from './mypage-진헌/MyComments/MyComments';
import MyPosts from './mypage-진헌/MyPosts/MyPosts';
import OthersPage from './mypage-진헌/otherspage/OthersPage';
import ID_search from './login-나은,정우, 진헌/ID_search';
import Community_Author from './authors-효진/Community_Author';
import Community_AuthorWrite from './authors-효진/Community_AuthorWrite';
import Community_AuthorDetail from './authors-효진/Community_AuthorDetail';
import Community_AuthorUpdate from './authors-효진/Community_AuthorUpdate';
import KakaoSignup from './login-나은,정우, 진헌/KakaoSignup';
import CommunityList from './community-재성/CommunityList';
import CommunityDetail from './community-재성/CommunityDetail';
import CommunityWrite from './community-재성/CommunityWrite';
import CommunityUpdate from './community-재성/CommunityUpdate';
import CommunityBest from './community-재성/CommunityBest';
import AuthorDetail from './webtoon-정우,윤호/AuthorDetail';
import WebtoonReviewComm from './webtoon-정우,윤호/WebtoonReviewComm';
import Community_AuthorMenu from './authors-효진/Community_AuthorMenu';
import AnonymousCommunityList from './anonymousCommunity-재성/AnonymousCommunityList';
import AnonymousCommunityDetail from './anonymousCommunity-재성/AnonymousCommunityDetail';
import AnonymousCommunityWrite from './anonymousCommunity-재성/AnonymousCommunityWrite';
import AnonymousCommunityUpdate from './anonymousCommunity-재성/AnonymousCommunityUpdate';
import AnonymosCommunityBest from './anonymousCommunity-재성/AnonymosCommunityBest';
import SurveyClient from './survey/SurveyClient';
import SurveyAddForm from './survey/SurveyAddForm';
import SurveyClientResult from './survey/SurveyClientResult';
import SurveyList from './survey/SurveyList';
import Delete from './login-나은,정우, 진헌/Delete';
import OneByOneQnA from './mypage-진헌/OneByOneQnA/OneByOneQnA';
import Managing_Report_Management_List from './manager-경민, 채린/manager_managerPage/Managing_Report_Management_List';
import Managing_Reviews from './manager-경민, 채린/manager_managerPage/Managing_Reviews';
import Manager_RegisteredMember from './manager-경민, 채린/manager_managerPage/Manager_RegisteredMember';
import Manager_MemberChart from './manager-경민, 채린/manager_managerPage/Manager_MemberChart';
import Manager_MemberManagement from './manager-경민, 채린/manager_managerPage/Manager_MemberManagement';
import Manager_ManagerPage from './manager-경민, 채린/manager_managerPage/Manager_ManagerPage';
import Manager_QnA_Answer from './manager-경민, 채린/manager_QnA/Manager_QnA_Answer';
import Manager_Notice_Form from './manager-경민, 채린/manager_Notice/Manager_Notice_Form';
import Manager_FAQ from './manager-경민, 채린/manager_FAQ/Manager_FAQ';
import Manager_QnA_Detail from './manager-경민, 채린/manager_QnA/Manager_QnA_Detail';
import Manager_QnA from './manager-경민, 채린/manager_QnA/Manager_QnA';
import Manager_QnA_Page from './manager-경민, 채린/manager_QnA/Manager_QnA_Page';
import Manager_QnA_Form from './manager-경민, 채린/manager_QnA/Manager_QnA_Form';
import Manager_Notice from './manager-경민, 채린/manager_Notice/Manager_Notice';
import Manager_Notice_Dtail from './manager-경민, 채린/manager_Notice/Manager_Notice_Dtail';
import SignUp from './login-나은,정우, 진헌/SignUp';
import PasswordlessRegister from './login-나은,정우, 진헌/PasswordlessRegister';
import { useSelector } from 'react-redux';
import { RootState } from './redux';
import { useEffect, useState } from 'react';

function App() {

  const permission = useSelector((state: RootState) => state.auth.permission);

  const [state, setState] = useState(false);

  const State = () => {
    if (permission === 1) {
      console.log("유저, USER")
    } else if (permission === 63) {
      console.log("기자, EDITOR")
    } else if (permission === 127) {
      setState(true);
      console.log("관리자, MANAGER")
    } else {
      console.log("권한 없음, UNKNOWN")
    }
  }

  console.log(permission);

  return (
    <BrowserRouter>
      <Header />
      <Routes>
        <Route path='/' element={<Home />} />

        {/* ===================login==================== */}
        <Route path='/login/Login_Main' element={<Login />} />
        <Route path='/login/signup' element={<SignUp />} />
        <Route path='/login/kakaoSignup' element={<KakaoSignup />} />
        <Route path='/login/Id_search' element={<ID_search />} />
        <Route path='/login/Password_search' element={<PasswordSearch />} />
        <Route path='/login/Delete' element={<Delete />} />

        {/* ===================manager==================== */}
        <Route path='/user/Notice_Detail/:num' element={<Manager_Notice_Dtail />} />
        <Route path='/user/Notice_List' element={<Manager_Notice />} />
        <Route path='/user/QnA_Form' element={<Manager_QnA_Form />} />
        <Route path='/user/QnA_List/:page' element={<Manager_QnA_Page />} />
        <Route path='/user/QnA_List' element={<Manager_QnA />} />
        <Route path='/user/QnA_Detail/:num' element={<Manager_QnA_Detail />} />
        <Route path='/user/FAQ_List' element={<Manager_FAQ />} />
        <Route path='/manager/Notice_Form' element={<Manager_Notice_Form />} />
        <Route path='/manager/QnA_Answer/:num' element={<Manager_QnA_Answer />} />
        <Route path='/manager/ManagerPage' element={<Manager_ManagerPage />} />
        <Route path='/manager/MemberManagement' element={<Manager_MemberManagement />} />
        <Route path='/manager/MemberChart' element={<Manager_MemberChart />} />
        <Route path='/manager/RegisteredMember' element={<Manager_RegisteredMember />} />
        <Route path='/manager/ManagingReviews' element={<Managing_Reviews />} />
        <Route path='/manager/ReportManagementList' element={<Managing_Report_Management_List />} />

        {/* =================survey-효진================= */}
        <Route path="/survey" element={<SurveyClient />} />
        <Route path="/manager/survey/surveyadd" element={<SurveyAddForm />} />
        <Route path="/manager/survey/surveylist" element={<SurveyList />} />
        <Route path="/manager/survey/back/surveyResult" element={<SurveyClientResult />} />
        {/* 위 두개 보류 */}

        {/* =================webtoon==================== */}
        <Route path="/webtoon/list/:page" element={<WebtoonList />} />
        <Route path="/webtoon/detail/:titleId/:page" element={<WebtoonDetail />} />
        <Route path="/webtoon/review/:titleId" element={<WebtoonReview />} />
        <Route path='/webtoon/author/:authorNum/:authorName' element={<AuthorDetail />} />
        <Route path="/webtoon/review/comm/:titleId/:webtoonReviewNum" element={<WebtoonReviewComm />} />

        {/* =================mypage====================== */}
        <Route path="/mypage/Mypage_Main" element={<MyPage />} />
        <Route path="/mypage/Mypage_Menu" element={<MyPageMenu />} />
        <Route path='/mypage/Profile_Edit' element={<ProfileEdit />} />
        <Route path="/mypage/Saved_Posts" element={<SavedPosts />} />
        <Route path="/mypage/Security_Settings" element={<SecuritySettings />} />
        <Route path="/mypage/MySinGO" element={<MySinGO />} />
        <Route path="/mypage/FollowedUsers" element={<FollowedUsers />} />
        <Route path="/mypage/MyComments" element={<MyComments />} />
        <Route path="/mypage/MyPosts" element={<MyPosts />} />
        <Route path="/mypage/OthersPage/:userNum" element={<OthersPage />} />
        <Route path="/mypage/OneByOneQnA" element={<OneByOneQnA />} />

        {/* =================coummunity====================== */}
        <Route path="/cumm/Cummu_List/:page" element={<CommunityList />} />
        <Route path="/cumm/Cummu_Detail/:communityNum" element={<CommunityDetail />} />
        <Route path="/cumm/Cummu_Form" element={<CommunityWrite />} />
        <Route path="/cumm/Cummu_Update/:communityNum" element={<CommunityUpdate />} />
        <Route path="/cumm/Cummu_Best/:page" element={<CommunityBest />} />

        {/* =================anonymousCommunity-재성================= */}
        <Route path="/cumm/Anonymous_Cummu_List/:page" element={<AnonymousCommunityList />} />
        <Route path="/cumm/Anonymous_Cummu_Detail/:communityNum" element={<AnonymousCommunityDetail />} />
        <Route path="/cumm/Anonymous_Cummu_Form" element={<AnonymousCommunityWrite />} />
        <Route path="/cumm/Anonymous_Cummu_Best/:page" element={<AnonymosCommunityBest />} />
        <Route path="/cumm/Anonymous_Cummu_Update/:communityNum" element={<AnonymousCommunityUpdate />} />

        {/* =================coummunity-효진================= */}
        <Route path="/Community_Author/Community_Author" element={<Community_Author />} />
        <Route path="/Community_Author/Community_AuthorMenu" element={<Community_AuthorMenu />} />
        <Route path="/Community_Author/Community_AuthorWrite" element={<Community_AuthorWrite />} />
        <Route path="/Community_Author/Community_AuthorDetail/:communityEditorNum" element={<Community_AuthorDetail />} />
        <Route path="/Community_Author/Community_AuthorUpdate/:communityEditorNum" element={<Community_AuthorUpdate />} />

        {/* ================= passwordless Login 윤호 ====================== */}
        <Route path='/login/passwordless' element={<PasswordlessRegister />} />

      </Routes>
      <Footer />
    </BrowserRouter>
  );
}

export default App;
