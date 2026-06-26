package com.sanju.marks;

import jakarta.persistence.*;

/**
 * A junction entity representing a student's academic performance.
 * Maps a specific Student (AppUser) to a specific Subject and stores their numerical score.
 *
 * @author Sanjib Murmu
 */

@Entity
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private AppUser student;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    private int marks;

    public Grade() {}

    public Grade(AppUser student, Subject subject, int marks) {
        this.student = student;
        this.subject = subject;
        this.marks = marks;
    }

    public Long getId() { return id; }
    public AppUser getStudent() { return student; }
    public void setStudent(AppUser student) { this.student = student; }
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    public int getMarks() { return marks; }
    public void setMarks(int marks) { this.marks = marks; }
}
