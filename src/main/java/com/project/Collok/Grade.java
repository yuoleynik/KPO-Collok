package com.project.Collok;

public class Grade {
    private int grade;
    private int student_id;
    private String subject;

    public Grade(int grade, String subject, int student_id) {
        this.grade = grade;
        this.student_id = student_id;
        this.subject = subject;
    }

    public int getGrade() {
        return grade;
    }

    public int getStudentId() {
        return student_id;
    }

    public String getSubject() {
        return subject;
    }

}
