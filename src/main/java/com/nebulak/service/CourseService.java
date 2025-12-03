package com.nebulak.service;


import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.nebulak.dto.CourseWithContentDTO;
import com.nebulak.dto.SectionWithTopicsDTO;
import com.nebulak.dto.TopicDTO;
import com.nebulak.model.Course;
import com.nebulak.model.Role;
import com.nebulak.model.Section;
import com.nebulak.model.Topic;
import com.nebulak.model.User;
import com.nebulak.model.UserRoles;
import com.nebulak.model.Role;

import com.nebulak.repository.CourseRepository;
import com.nebulak.repository.TopicRepository;
import com.nebulak.repository.SectionRepository; 

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private SectionRepository sectionRepository; 
    @Autowired
    private S3Service s3Service; 
    public Course createCourse(Course course) {
        course.setCreatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }
    
 public Page<Course> findCourses(Long categoryId, String keyword, String duration, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Specification<Course> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate titleLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likePattern);
                Predicate descriptionLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern);
                predicates.add(criteriaBuilder.or(titleLike, descriptionLike));
            }
            if (duration != null && !duration.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("duration"), duration));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return courseRepository.findAll(spec, pageable);
    }
    

    @Transactional
    public void deleteCourseAndContent(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));

        if (course.getSections() != null) {
            for (Section section : course.getSections()) {
                deleteSectionVideos(section);
            }
        }
        
        String imageUrl = course.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            s3Service.deleteFile(imageUrl);
        }

        courseRepository.delete(course);
    }
    
    
    private void deleteSectionVideos(Section section) {
        if (section.getTopics() != null) {
            for (Topic topic : section.getTopics()) {
                String videoUrl = topic.getVideoUrl();
                if (videoUrl != null && !videoUrl.isEmpty()) {
                    s3Service.deleteFile(videoUrl);
                }
            }
        }
    }


    public Course getCourseById(Long courseId) {
	    
     Course course=courseRepository.getById(courseId);
     return course;
    }
    @Transactional
    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }
    @Transactional
    public Course addSectionToCourse(Long courseId, Section section) {
	 Optional<Course> courseOptional = courseRepository.findById(courseId); 
     
     if (courseOptional.isEmpty()) {
         throw new RuntimeException("Course not found with ID: " + courseId);
     }
     
     Course course = courseOptional.get();
     section.setCourse(course); 
     course.getSections().add(section); 
     return courseRepository.save(course);
	
    }

    @Transactional
    public CourseWithContentDTO getCourseWithSectionsAndTopics(Long courseId) {
        // ... (Existing implementation for fetching course content)
        Optional<Course> courseOptional = courseRepository.findById(courseId);

        if (courseOptional.isEmpty()) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }

        Course course = courseOptional.get();

        List<SectionWithTopicsDTO> sectionDTOs = course.getSections().stream()
                .sorted(Comparator.comparing(Section::getSectionOrder))
                .map(section -> {
                    List<TopicDTO> topicDTOs = section.getTopics().stream()
                            .sorted(Comparator.comparing(Topic::getTopicOrder))
                            .map(topic -> new TopicDTO(
                                    topic.getId(),
                                    topic.getTitle(),
                                    topic.getTopicOrder()
                                    
                            ))
                            .collect(Collectors.toList());

                    return new SectionWithTopicsDTO(
                            section.getId(),
                            section.getTitle(),
                            section.getSectionOrder(),
                            section.getDescription(),
                            topicDTOs
                    );
                })
                .collect(Collectors.toList());

        return new CourseWithContentDTO(
                course.getId(),
                course.getTitle(),
                course.getImageUrl(), 
                sectionDTOs
        );
    }
    
    @Transactional
    public void deleteSectionFromCourse(Long courseId, Long sectionId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);

        if (courseOptional.isEmpty()) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }

        Course course = courseOptional.get();

        Optional<Section> sectionToDeleteOptional = course.getSections().stream()
                .filter(section -> section.getId().equals(sectionId))
                .findFirst();

        if (sectionToDeleteOptional.isEmpty()) {
            throw new RuntimeException("Section not found with ID: " + sectionId + " in course " + courseId);
        }

        Section sectionToDelete = sectionToDeleteOptional.get();

        deleteSectionVideos(sectionToDelete);

        course.getSections().remove(sectionToDelete);
        
       
        sectionToDelete.setCourse(null); 

        sectionRepository.delete(sectionToDelete); 
      
    }

    @Transactional 
    public Topic addTopicToSection(Long courseId, Long sectionId, Topic topic) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        
        Section section = course.getSections().stream()
                .filter(s -> s.getId().equals(sectionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Section not found with ID: " + sectionId + " in course " + courseId));

        topic.setSection(section);
        section.getTopics().add(topic); 
        Topic savedTopic = topicRepository.save(topic); 
        
        return savedTopic;
    }
    @Transactional
    public void deleteTopicFromSection(Long courseId, Long sectionId, Long topicId) {
        courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        Section section = sectionRepository.findById(sectionId)
            .orElseThrow(() -> new RuntimeException("Section not found with ID: " + sectionId));
        
        if (!section.getCourse().getId().equals(courseId)) {
            throw new RuntimeException("Section ID " + sectionId + " does not belong to Course ID: " + courseId);
        }
        
        Topic topic = topicRepository.findById(topicId)
            .orElseThrow(() -> new RuntimeException("Topic not found with ID: " + topicId));

        if (!topic.getSection().getId().equals(sectionId)) {
            throw new RuntimeException("Topic ID " + topicId + " does not belong to Section ID: " + sectionId);
        }

        String videoUrl = topic.getVideoUrl();
        if (videoUrl != null && !videoUrl.isEmpty()) {
            try {
                s3Service.deleteFile(videoUrl);
            } catch (Exception e) {
                System.err.println("Warning: Failed to delete S3 file for Topic " + topicId + ": " + e.getMessage());
            }
        }

        topicRepository.delete(topic);
    }

	public Page<Course> findAdminCourses(int minLevel, Long categoryId, String keyword, String duration, int pageNumber, int pageSize) {
		// TODO Auto-generated method stub
		System.out.print("minlev"+minLevel);
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Specification<Course> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate titleLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likePattern);
                Predicate descriptionLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern);
                predicates.add(criteriaBuilder.or(titleLike, descriptionLike));
            }
            if (duration != null && !duration.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("duration"), duration));
            }
            Join<Course, User> ownerJoin = root.join("owner");

            
            Join<User, UserRoles> userRolesJoin = ownerJoin.join("roles"); 
            Join<UserRoles, Role> roleJoin = userRolesJoin.join("role"); 

            Predicate roleLevelCondition = criteriaBuilder.greaterThanOrEqualTo(
                roleJoin.get("level"), 
                minLevel
            );
            
            predicates.add(roleLevelCondition);

            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return courseRepository.findAll(spec, pageable);
	}

	public boolean isUserEnrolledForTopic(String email, Long topicId) {
        if (!topicRepository.existsById(topicId)) {
             throw new RuntimeException("Topic not found with ID: " + topicId);
        }
        return true; 
    }
	public String getVideoUrlForTopic(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
            .orElseThrow(() -> new RuntimeException("Topic not found with ID: " + topicId));
        
        return topic.getVideoUrl();
    }

	public Boolean getTopicAccess(int minLevel, Long topicId) {
		List<UserRoles> roles=topicRepository.findById(topicId).get().getSection().getCourse().getOwner().getRoles();
		int level=roles.stream().map(UserRoles::getRole).mapToInt(Role::getLevel).min().getAsInt();
		if(minLevel<=level){
			return true;
		}
		return false;
	}

	

	
}