package com.study.train.member.req;

import lombok.Data;

@Data
public class MemberSaveReq {

    private Long id;
    private String mobile;
    private Boolean isSchoolAdmin;
}
