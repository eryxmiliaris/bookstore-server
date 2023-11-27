package com.vb.bookstore.payloads.user;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate birthDate;
    private List<String> roles;
    private boolean hasActiveSubscription;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate activeSubscriptionEndDate;
}

