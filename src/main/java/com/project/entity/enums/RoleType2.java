package com.project.entity.enums;

public enum RoleType2 {

    ADMIN("Admin"),
    TEACHER("Teacher"),
    STUDENT("Student"),
    MANAGER("Dean"),
    ASSISTANT_MANAGER("ViceDean");

    public final String name;

    RoleType2(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }
}