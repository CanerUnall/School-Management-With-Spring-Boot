package com.project.entity.enums;

import lombok.Getter;

@Getter
public enum RoleType {

    STUDENT("Student"),
    TEACHER("Teacher"),
    ASSISTANT_MANAGER("ViceDean"),
    MANAGER("Dean"),
    ADMIN("Admin");
    public final String name;


    RoleType(String name) {
        this.name = name;

    }

}
