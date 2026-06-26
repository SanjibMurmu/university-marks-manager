package com.sanju.marks;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Core service layer responsible for orchestrating the business logic of the Jadavpur University Portal.
 * <p>
 * This centralized service manages a unified user system to handle Role-Based Access 
 * Control (RBAC) across Students, Teachers, and System Administrators. It acts as the 
 * primary intermediary between the REST controllers and the data access repositories.
 * </p>
 * * <p><b>Key Responsibilities:</b></p>
 * <ul>
 * <li><b>Authentication and Security:</b> Validates user credentials and enforces strict role boundaries during login.</li>
 * <li><b>Automated Provisioning:</b> Seeds the database with master accounts and testing data upon initial deployment.</li>
 * <li><b>Account Recovery:</b> Manages the secure, token-based password reset lifecycle, including direct SMTP email dispatch.</li>
 * <li><b>Administrative Operations:</b> Handles the creation and safe deletion of users and academic subjects, ensuring relational database integrity.</li>
 * <li><b>Data Aggregation:</b> Transforms raw relational database entities into flattened DTOs for optimal React frontend consumption.</li>
 * </ul>
 * * @author Sanjib Murmu
 * @version 1.0
 * @see AppUser
 * @see StudentResultDTO
 */

@Service
public class StudentService {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final GradeRepository gradeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

   public StudentService(UserRepository userRepository, SubjectRepository subjectRepository, GradeRepository gradeRepository, PasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.gradeRepository = gradeRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender; 
    }

    //THE SEEDER
    /**
     * Auto-generates initial database records on application startup if the database is empty.
     * Seeds master administrative accounts and default testing data to ensure 
     * immediate functionality after a fresh cloud deployment.
     */
    public void initializeDatabase() {
        System.out.println("🚨 [SEEDER] Checking database...");
        
        try {
            long count = userRepository.count();
            System.out.println("🚨 [SEEDER] Current user count in Aiven is: " + count);

            if (count == 0) {
                System.out.println("🚨 [SEEDER] Database is empty. Building master accounts with emails...");
                
                // Creating Master Accounts
                AppUser admin = new AppUser("admin", passwordEncoder.encode("admin123"), "admin", "System Administrator", adminEmail);
                AppUser teacher = new AppUser("teacher", passwordEncoder.encode("teacher123"), "teacher", "Professor Smith", "smith@jadavpuruniversity.in");
                AppUser student = new AppUser("103", passwordEncoder.encode("mysecretpassword"), "student", "Samir", "samir@jadavpuruniversity.in");
                
                userRepository.saveAll(List.of(admin, teacher, student));

                Subject oos = new Subject("OOS", teacher);
                Subject cn = new Subject("CN", teacher);
                Subject maths = new Subject("Maths", teacher);
                subjectRepository.saveAll(List.of(oos, cn, maths));

                gradeRepository.save(new Grade(student, oos, 88));
                gradeRepository.save(new Grade(student, cn, 92));
                gradeRepository.save(new Grade(student, maths, 85));
                
                System.out.println("✅ Relational Database Initialized!");
            } else {
                System.out.println("🚨 [SEEDER] Database already has data. Skipping generation.");
            }
        } catch (Exception e) {
            System.out.println("❌ [SEEDER FATAL CRASH] The database rejected the data:");
            e.printStackTrace();
        }
    }

    //UNIFIED AUTHENTICATOR
    public String authenticate(String role, String username, String rawPassword) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Error: User not found."));

        if (!user.getRole().equalsIgnoreCase(role)) {
            throw new IllegalArgumentException("Error: Invalid role selected for this account.");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Error: Incorrect password.");
        }

        return user.getUsername();
    }

    /**
     * Executes the password recovery flow by verifying the user's registered email.
     * Generates a secure, time-sensitive 6-digit verification token and dispatches 
     * it directly to the user's inbox via SMTP.
     *
     * @param username The account identifier requesting the reset.
     * @param email The registered email address associated with the account.
     * @throws IllegalArgumentException If the account is not found or the email does not match.
     */
    @Transactional
    public void forgotPassword(String username, String email) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Error: Account not found for username: " + username));

        if (user.getEmail() == null || !user.getEmail().equalsIgnoreCase(email)) {
            throw new IllegalArgumentException("Error: The provided email does not match our records.");
        }

        String token = String.format("%06d", new Random().nextInt(999999));
        
        user.setResetToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

      // Creating and sending the actual email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(adminEmail); 
        message.setTo(email);
        message.setSubject("Jadavpur University Portal - Password Recovery");
        
        String emailBody = "Hello " + user.getFullName() + ",\n\n"
                + "A password recovery request was initiated for your account.\n"
                + "Your 6-digit verification code is: " + token + "\n\n"
                + "This code will expire in 15 minutes. If you did not request this, please ignore this email.\n\n"
                + "Regards,\n"
                + "JU IT Department System";
                
        message.setText(emailBody);
        
        try {
            mailSender.send(message);
            System.out.println("✅ Real email successfully dispatched to " + email);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error: Failed to send email. Check SMTP settings.");
        }
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        AppUser user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Error: Invalid, malformed, or used recovery token."));

        if (user.getTokenExpiry() == null || user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Error: The verification token has expired.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);
        
        System.out.println("🔒 [SECURITY] Password reset successfully for user: " + user.getUsername());
    }

    // --- ADMIN & DYNAMIC FETCH COMMANDS ---
    public String registerUser(String username, String rawPassword, String role, String fullName, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Error: User already exists.");
        }
        AppUser newUser = new AppUser(username, passwordEncoder.encode(rawPassword), role, fullName, email);
        userRepository.save(newUser);
        return "Success: " + role.toUpperCase() + " " + fullName + " successfully created!";
    }

    /**
     * Administrative method to permanently remove a user from the system.
     * Safely handles foreign key constraints by automatically cascading deletions 
     * to orphaned child entities (e.g., clearing a teacher's subjects before deletion).
     *
     * @param username The unique identifier of the user to delete.
     * @throws IllegalArgumentException If the user is not found in the database.
     */
    @Transactional
    public void deleteUser(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // If the user is a teacher, we MUST delete their assigned subjects first to avoid MySQL foreign key crashes
        if (user.getRole().equalsIgnoreCase("teacher")) {
            List<Subject> teacherSubjects = subjectRepository.findAll().stream()
                .filter(sub -> sub.getTeacher().getId().equals(user.getId()))
                .toList();
            subjectRepository.deleteAll(teacherSubjects);
        }
        
        userRepository.delete(user);
    }

    @Transactional
    public void deleteSubject(String subjectName) {
        Subject subjectToDelete = subjectRepository.findAll().stream()
                .filter(sub -> sub.getSubjectName().equalsIgnoreCase(subjectName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
                
        subjectRepository.delete(subjectToDelete);
    }

    public List<StudentResultDTO> getAllResults() {
        List<StudentResultDTO> results = new ArrayList<>();
        List<AppUser> students = userRepository.findAll().stream()
                .filter(u -> u.getRole().equalsIgnoreCase("student"))
                .toList();

        for (AppUser student : students) {
            Long rollNo;
            try {
                rollNo = Long.parseLong(student.getUsername());
            } catch (NumberFormatException e) {
                continue;
            }
            StudentResultDTO dto = new StudentResultDTO(rollNo, student.getFullName());
            List<Grade> grades = gradeRepository.findByStudentId(student.getId());
            
            for (Grade g : grades) {
                switch (g.getSubject().getSubjectName()) {
                    case "OOS" -> dto.setOos(g.getMarks());
                    case "CN" -> dto.setCn(g.getMarks());
                    case "Maths" -> dto.setMaths(g.getMarks());
                }
            }
            results.add(dto);
        }
        return results;
    }

    public String createSubject(String subjectName, String teacherUsername) {
    AppUser teacher = userRepository.findByUsername(teacherUsername)
            .orElseThrow(() -> new IllegalArgumentException("Error: Teacher account not found."));
    
    if (!teacher.getRole().equalsIgnoreCase("teacher")) {
        throw new IllegalArgumentException("Error: Assigned user must have the 'teacher' role.");
    }
    
    Subject newSubject = new Subject(subjectName, teacher);
    subjectRepository.save(newSubject);
    return "Success: Subject " + subjectName + " assigned to " + teacher.getFullName();
}

// --- 5. FETCH ALL SUBJECTS FOR ADMIN DIRECTORY ---
    public List<Map<String, String>> getAllSubjects() {
        return subjectRepository.findAll().stream().map(subject -> {
            Map<String, String> subjectData = new HashMap<>();
            subjectData.put("subjectName", subject.getSubjectName());
            subjectData.put("teacherUsername", subject.getTeacher().getUsername()); 
            return subjectData;
        }).toList();
    }
}