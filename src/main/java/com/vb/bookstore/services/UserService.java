package com.vb.bookstore.services;

import com.vb.bookstore.entities.User;
import com.vb.bookstore.payloads.user.UpdateUserInfoRequest;
import com.vb.bookstore.payloads.user.UpdateUserInfoResponse;
import com.vb.bookstore.payloads.user.UserDTO;

public interface UserService {
    User getCurrentUser();

    boolean currentUserIsAdmin();

    UserDTO getUserInfo();

    UpdateUserInfoResponse updateUserInfo(UpdateUserInfoRequest request);
}
