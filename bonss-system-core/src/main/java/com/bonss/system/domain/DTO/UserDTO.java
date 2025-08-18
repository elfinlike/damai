package com.bonss.system.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;

    private String nickName;

    private String phonenumber;

    private Integer gender;

    private LocalDate birth;
}

