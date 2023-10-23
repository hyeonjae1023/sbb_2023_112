package com.sbs.exam2.model;

import lombok.Data;

@Data
public class KakaoProfile {

    public Long id;
    public String connected_at;
    public Properties properties;
    public Kakao_account kakao_account;
    @Data
    class Properties {

        public String nickname;
        public String profile_image;
        public String thumbnail_image;

    }
    @Data
    class Kakao_account {

        public Boolean profile_nickname_needs_agreement;
        public Boolean profile_image_needs_agreement;
        public Profile profile;
        @Data
        class Profile {

            public String nickname;
            public String thumbnail_image_url;
            public String profile_image_url;
            public Boolean is_default_image;

        }
    }
}




