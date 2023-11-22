package com.vb.bookstore.payloads.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private LocalDate birthDate;
    private List<String> roles;
    private boolean hasActiveSubscription;
    private LocalDate activeSubscriptionEndDate;
}

