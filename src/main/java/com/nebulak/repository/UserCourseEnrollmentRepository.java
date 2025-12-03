package com.nebulak.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nebulak.model.UserCourseEnrollment;
import com.nebulak.model.User;
import com.nebulak.model.Course;

public interface UserCourseEnrollmentRepository extends JpaRepository<UserCourseEnrollment, Long> {
    
    boolean existsByUserAndCourse(User user, Course course);

    UserCourseEnrollment findByUserAndCourse(User user, Course course);
}