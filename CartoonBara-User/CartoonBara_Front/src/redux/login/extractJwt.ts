const extractToken = (token: string | null): string[] => {
    // token이 null, undefined 또는 빈 문자열일 경우 빈 배열 반환
    if (!token) {
      return [];
    }
  
    const parts = token.split('.'); // 토큰을 '.'으로 분리
  
    // 토큰의 형식이 맞지 않으면 오류를 출력하고 빈 배열 반환
    if (parts.length !== 3) {
      console.error("Invalid JWT token");
      return [];
    }
  
    const payload = parts[1]; // 두 번째 부분이 페이로드
    let decodedPayload;
  
    try {
      decodedPayload = JSON.parse(atob(payload)); // Base64로 디코딩 후 JSON으로 파싱
    } catch (error) {
      console.error("Failed to decode JWT token");
      return [];
    }
  
    // authority가 배열일 경우 반환, 그렇지 않으면 빈 배열 반환
    if (decodedPayload && Array.isArray(decodedPayload.authority)) {
      return decodedPayload.authority;
    }
  
    // authority가 배열이 아닌 경우 빈 배열 반환
    return [];
  };
  
  export default extractToken;