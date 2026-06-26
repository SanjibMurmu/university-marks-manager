package com.sanju.marks;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Spring Data JPA repository for executing CRUD operations and custom queries 
 * against the underlying Aiven MySQL database.
 *
 * @author Sanjib Murmu
 */

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findBySubjectName(String subjectName);
}
