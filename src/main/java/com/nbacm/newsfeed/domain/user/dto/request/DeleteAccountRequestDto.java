package com.nbacm.newsfeed.domain.user.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteAccountRequestDto {
    private String password;
}
