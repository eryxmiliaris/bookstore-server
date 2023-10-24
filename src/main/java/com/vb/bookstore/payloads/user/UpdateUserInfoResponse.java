package com.vb.bookstore.payloads.user;

import com.vb.bookstore.payloads.MessageResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserInfoResponse {
    private ResponseCookie jwtCookie;
    private MessageResponse messageResponse;
}
