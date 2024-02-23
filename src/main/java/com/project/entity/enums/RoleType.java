package com.project.entity.enums;

public enum RoleType {

    STUDENT("Student",1),
    TEACHER("Teacher",2),
    ASSISTANT_MANAGER("ViceDean",3),
    MANAGER("Dean",4),
    ADMIN("Admin",5);
    public final String name;
    public final int rank;

    RoleType(String name, int rank) {
        this.name = name;
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public int getRank() {
        return rank;
    }
}
