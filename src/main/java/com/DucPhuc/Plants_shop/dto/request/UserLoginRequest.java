package com.DucPhuc.Plants_shop.dto.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserLoginRequest {
    private String username;
    private String password;
}
