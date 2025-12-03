package com.nebulak.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.nebulak.model.Course;
import com.nebulak.model.User;
import com.nebulak.model.UserCourseEnrollment;
import com.nebulak.repository.UserCourseEnrollmentRepository;

import jakarta.transaction.Transactional;

@Service
public class UserCourseEnrollmentService {

    private final UserCourseEnrollmentRepository enrollmentRepository;

    public UserCourseEnrollmentService(UserCourseEnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional
    public UserCourseEnrollment enrollUserInCourse(User user, Course course) {
        if (enrollmentRepository.existsByUserAndCourse(user, course)) {
            return enrollmentRepository.findByUserAndCourse(user, course);
        }

        UserCourseEnrollment enrollment = new UserCourseEnrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        
        return enrollmentRepository.save(enrollment);
    }
}