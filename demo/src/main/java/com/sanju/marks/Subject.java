package com.sanju.marks;

import jakarta.persistence.*;

/**
 * Represents an academic course within the IT Department.
 * Maintains a Many-To-One relationship with the AppUser entity to assign 
 * specific subjects to designated teaching staff.
 *
 * @author Sanjib Murmu
 */

@Entity
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String subjectName; // e.g., "Computer Networks"

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private AppUser teacher;

    public Subject() {}

    public Subject(String subjectName, AppUser teacher) {
        this.subjectName = subjectName;
        this.teacher = teacher;
    }

    public Long getId() { return id; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public AppUser getTeacher() { return teacher; }
    public void setTeacher(AppUser teacher) { this.teacher = teacher; }
}
