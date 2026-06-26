package com.sanju.marks;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Spring Data JPA repository for executing CRUD operations and custom queries 
 * against the underlying Aiven MySQL database.
 *
 * @author Sanjib Murmu
 */

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentId(Long studentId);
    List<Grade> findBySubjectId(Long subjectId);
}
