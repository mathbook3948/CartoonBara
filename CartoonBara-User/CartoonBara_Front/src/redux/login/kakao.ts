// src/kakao.d.ts
declare global {
    interface KakaoProfile {
        nickname: string;
        thumbnail_image_url: string;
        profile_image_url: string;
    }

    interface KakaoAccount {
        email: string;
        profile: KakaoProfile;
    }

    interface KakaoUser {
        id: number;
        kakao_account: KakaoAccount;
    }

    interface KakaoAuth {
        access_token: string;
    }

    interface KakaoAPI {
        request: (options: {
            url: string;
            success: (response: any) => void;
            fail: (error: any) => void;
        }) => void;
    }

    interface Kakao {
        init: (appKey: string) => void;
        Auth: {
            login: (options: {
                scope: string;
                success: (authObj: KakaoAuth) => void;
                fail: (error: any) => void;
            }) => void;
            logout: (callback: () => void) => void;
        };
        API: KakaoAPI;
    }

    interface Window {
        Kakao: Kakao;
    }
}

export { };