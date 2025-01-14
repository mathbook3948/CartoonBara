// login/reducers/index.ts
import { combineReducers } from 'redux';
import ProfileSlice from './ProfileSlice';

//// 여러 리듀서를 하나로 결합
const rootReducer = combineReducers({
// authReducer는 'auth' 상태 조각을 관리
  auth : ProfileSlice
});
// 루트 리듀서의 반환 타입을 정의하여, 상태 트리의 타입을 유추
export type RootState = ReturnType<typeof rootReducer>;
// 결합된 루트 리듀서를 내보낸다.
export default rootReducer