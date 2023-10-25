package com.konkuk.vecto.security.validator;

import com.konkuk.vecto.security.dto.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 유저 회원가입 및 수정 form을 검증하는 validator
@Component
@Slf4j
public class UserValidator extends UserValidatorFunc implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return UserRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserRequest req = (UserRequest) target;

        if(req.getProvider() == null || req.getProvider().isEmpty()) {
            errors.rejectValue("provider", "NotEmpty",
                    "로그인 유형을 입력해주세요.");
            return;
        }

        if (req.getProvider().equals("vecto")) {

            validateUserId(req.getUserId(), errors, req.getRequestType(), "vecto");

            validateUserPw(req.getUserPw(), errors, req.getRequestType());

            validateNickName(req.getNickName(), errors, req.getRequestType());

            validateEmail(req.getEmail(), errors, req.getRequestType());

        } else if (req.getProvider().equals("kakao")) {

            validateUserId(req.getUserId(), errors, req.getRequestType(), "kakao");

            validateNickName(req.getNickName(), errors, req.getRequestType());

        } else {
            errors.rejectValue("provider", "Pattern",
                    "로그인 유형은 \"vecto\" 또는 \"kakao\" 로 입력해주세요.");
        }
    }
}
