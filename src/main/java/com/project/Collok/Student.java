package com.project.Collok;

class Student {
    private String name;
    private int age;
    private String specification;

    public Student(String name, int student_id, int age, String specification) {
        this.name = name;
        this.age = age;
        this.specification = specification;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getSpecification() {
        return specification;
    }
}