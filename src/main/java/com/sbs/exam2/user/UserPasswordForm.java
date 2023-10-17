package com.sbs.exam2.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPasswordForm {
    @Size(max = 25)
    @NotEmpty(message = "사용자ID는 필수항목입니다.")
    private String username;

    @NotEmpty(message = "현재 비밀번호는 필수항목입니다.")
    private String currentPassword;

    @NotEmpty(message = "새 비밀번호는 필수항목입니다.")
    private String newPassword;

    @NotEmpty(message = "새 비밀번호는 필수항목입니다.")
    private String newPassword2;
}
