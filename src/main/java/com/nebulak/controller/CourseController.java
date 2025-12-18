package com.nebulak.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.nebulak.dto.CourseDTO;
import com.nebulak.dto.CourseWithContentDTO;
import com.nebulak.dto.SectionDTO;
import com.nebulak.model.Category;
import com.nebulak.model.Course;
import com.nebulak.model.Section;
import com.nebulak.model.Topic;
import com.nebulak.model.User;
import com.nebulak.model.UserRoles;
import com.nebulak.service.CategoryService;
import com.nebulak.service.CourseService;
import com.nebulak.service.S3Service;
import com.nebulak.config.JwtProvider;


@RestController
@RequestMapping("/courses")
public class CourseController {

	@Autowired
	private CourseService courseService;
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private S3Service s3Service;
	
	@Autowired
	private CustomUserServiceImplementation customUserServiceImplementation;

	@Autowired
	private JwtProvider jwtProvider;
	
	@PostMapping("/addcategory")
	public ResponseEntity<Category> createCategory(@RequestBody Category category,@RequestHeader("Authorization") String authHeader) {
		String jwt = null;
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        jwt = authHeader; 
	    } else {
	        return ResponseEntity.status(401).build();
	    }
	    String email = jwtProvider.getEmailFromToken(jwt);
	    User userDetails = customUserServiceImplementation.loadUserDetailsByUsername(email);
	    
		Category savedCategory = categoryService.createCategory(category);
		return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
	}
	
	
	@PostMapping(value = "/addcourse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> addCourse(@RequestParam("title") String title,
			@RequestParam("description") String description, @RequestParam("instructorName") String instructorName,
			@RequestParam("price") Double price, @RequestParam("discountedPrice") Double discountedPrice,
			@RequestParam("duration") Integer duration, @RequestParam("categoryId") Long categoryId,
			@RequestPart("image") MultipartFile imageFile,
			@RequestHeader("Authorization") String authHeader) {
		
		String jwt = null;
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        jwt = authHeader; 
	    } else {
	        return ResponseEntity.status(401).build();
	    }
	    String email = jwtProvider.getEmailFromToken(jwt);
	    User userDetails = customUserServiceImplementation.loadUserDetailsByUsername(email);
	    

		try {
			String imageUrl = s3Service.uploadFile(imageFile);
			Course course = new Course();
			course.setTitle(title);
			course.setDescription(description);
			course.setInstructorName(instructorName);
			course.setPrice(price);
			course.setDiscountedPrice(discountedPrice);
			course.setDuration(duration);
			course.setImageUrl(imageUrl);
			course.setOwner(userDetails);

			Category category = categoryService.getCategory(categoryId);

			course.setCategory(category);
			courseService.createCourse(course);

			return ResponseEntity.ok("Successfully created course");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Error during file upload: " + e.getMessage());
		}
	}
	@GetMapping("/getcategories")
	public ResponseEntity<List<Category>> getAllCategories() {
	    List<Category> categories = categoryService.getAllCategories();
	    return new ResponseEntity<>(categories, HttpStatus.OK);
	}

	@PutMapping(value = "/editcourse/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> editCourse(@PathVariable("courseId") Long courseId,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "instructorName", required = false) String instructorName,
			@RequestParam(value = "price", required = false) Double price,
			@RequestParam(value = "discountedPrice", required = false) Double discountedPrice,
			@RequestParam(value = "duration", required = false) Integer duration,
			@RequestParam(value = "categoryId", required = false) Long categoryId,
			@RequestPart(value = "image", required = false) MultipartFile imageFile) {

		try {
			Course course = courseService.getCourseById(courseId);

			if (course == null) {
				return ResponseEntity.status(404).body("Error: Course not found with ID: " + courseId);
			}

			boolean changesMade = false;
			if (title != null && !title.equals(course.getTitle())) {
				course.setTitle(title);
				changesMade = true;
			}
			if (description != null && !description.equals(course.getDescription())) {
				course.setDescription(description);
				changesMade = true;
			}
			if (instructorName != null && !instructorName.equals(course.getInstructorName())) {
				course.setInstructorName(instructorName);
				changesMade = true;
			}
			if (price != null && !price.equals(course.getPrice())) {
				course.setPrice(price);
				changesMade = true;
			}
			if (discountedPrice != null && !discountedPrice.equals(course.getDiscountedPrice())) {
				course.setDiscountedPrice(discountedPrice);
				changesMade = true;
			}
			if (duration != null && !duration.equals(course.getDuration())) {
				course.setDuration(duration);
				changesMade = true;
			}

			if (categoryId != null && !categoryId.equals(course.getCategory().getId())) {
				Category category = categoryService.getCategory(categoryId);
				// Always check if the fetched category is valid before setting
				if (category != null) {
					course.setCategory(category);
					changesMade = true;
				} else {
					return ResponseEntity.status(400).body("Error: Category not found with ID: " + categoryId);
				}
			}
			if (imageFile != null && !imageFile.isEmpty()) {
				String oldImageUrl = course.getImageUrl();
				if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
					s3Service.deleteFile(oldImageUrl);
				}

				String newImageUrl = s3Service.uploadFile(imageFile);
				course.setImageUrl(newImageUrl);
				changesMade = true;
			}

			if (changesMade) {
				courseService.updateCourse(course);
				return ResponseEntity.ok("Successfully updated course with ID: " + courseId);
			} else {
				return ResponseEntity.ok("Course update request processed. No detectable changes were applied.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Error during course update: " + e.getMessage());
		}
	}

	@GetMapping("/getcourses")
	public ResponseEntity<Page<Course>> getAllCourses(
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) Long categoryId, @RequestParam(required = false) String keyword,
			@RequestParam(required = false) String duration 
	) {
		page = (page > 0) ? page - 1 : 0;
		Page<Course> coursesPage = courseService.findCourses(categoryId, keyword, duration, page, size);
		return ResponseEntity.ok(coursesPage);
	}
	@GetMapping("/admin/getcourses")
	public ResponseEntity<Page<Course>> getAllAdminCourses(
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) Long categoryId, @RequestParam(required = false) String keyword,
			@RequestParam(required = false) String duration ,
			@RequestHeader("Authorization") String authHeader
	) {
		String jwt = null;
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        jwt = authHeader; 
	    } else {
	        return ResponseEntity.status(401).build();
	    }
	    String email = jwtProvider.getEmailFromToken(jwt);
	    User userDetails = customUserServiceImplementation.loadUserDetailsByUsername(email);
	    List<UserRoles> roles= userDetails.getRoles();
	    int minLevel = roles.stream()
		        .map(userRole -> userRole.getRole().getLevel()) 
		        .min(Integer::compare)
		        .orElse(Integer.MAX_VALUE); // Use a safe default if the list is empty
		    System.out.print("minLevel: " + minLevel);    
		page = (page > 0) ? page - 1 : 0;
		Page<Course> coursesPage = courseService.findAdminCourses(minLevel, categoryId, keyword, duration, page, size);
		System.out.print("page"+coursesPage);
		return ResponseEntity.ok(coursesPage);
	}
	
	@GetMapping("/course/{courseId}")
	public ResponseEntity<CourseDTO> getCourseDetails(@PathVariable Long courseId) {
	    Course course = courseService.getCourseById(courseId);

	    if (course != null) {
	        // Map the Entity to the DTO
	        CourseDTO dto = new CourseDTO();
	        dto.setId(course.getId());
	        dto.setTitle(course.getTitle());
	       dto.setDescription(course.getDescription());
	     dto.setInstructorName(course.getInstructorName());
	     dto.setPrice(course.getPrice());
	     dto.setImageUrl(course.getImageUrl());
	     dto.setDiscountedPrice(course.getDiscountedPrice());
	     dto.setDuration(course.getDuration());
	     dto.setCategoryId(course.getCategory().getId());
	     dto.setCategory(course.getCategory());
	     dto.setSections(course.getSections());
	        return new ResponseEntity<>(dto, HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
	
	@GetMapping("/secure/video/{topicId}")
    public ResponseEntity<String> streamVideo(@PathVariable Long topicId, @RequestHeader("Authorization") String authHeader) {
        String jwt = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader; 
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401
        }
        
        String email = jwtProvider.getEmailFromToken(jwt);
        System.out.print("email"+email);
        try {
           
            User userDetails = customUserServiceImplementation.loadUserDetailsByUsername(email);
    	    List<UserRoles> roles= userDetails.getRoles();
    	    int minLevel = roles.stream()
    		        .map(userRole -> userRole.getRole().getLevel()) 
    		        .min(Integer::compare)
    		        .orElse(Integer.MAX_VALUE); // Use a safe default if the list is empty
    		    System.out.print("minLevel: " + minLevel); 
    		
    		Boolean topicAccess=courseService.getTopicAccess(minLevel,topicId);
    		if (!courseService.isUserEnrolledForTopic(email, topicId) || topicAccess==false) {
                 return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
             }
    	
            String fullS3UrlFromDB = courseService.getVideoUrlForTopic(topicId);

            if (fullS3UrlFromDB == null || fullS3UrlFromDB.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
            }
            System.out.println(fullS3UrlFromDB);
            final int EXPIRATION_SECONDS = 3600; 
            String signedUrl = s3Service.generateSignedUrl(fullS3UrlFromDB, EXPIRATION_SECONDS);
            System.out.println(signedUrl);

            return new ResponseEntity<>(signedUrl,HttpStatus.OK);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Topic not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
	@DeleteMapping("/deletecourse/{courseId}")
	public ResponseEntity<String> deleteCourse(@PathVariable Long courseId) {
	    try {
	        // CALL THE NEW METHOD
	        courseService.deleteCourseAndContent(courseId); 
	        return ResponseEntity.ok("Course with ID " + courseId + " and all associated content (image and videos) deleted successfully.");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error deleting course: " + e.getMessage());
	    }
	}
	
	@PostMapping("/addsection/{courseId}")
	public ResponseEntity<Section> addSectionToCourse(@PathVariable Long courseId, @RequestBody SectionDTO sectionDTO) {
		try {
		
			Section section = new Section();
			section.setTitle(sectionDTO.getTitle());
			section.setSectionOrder(sectionDTO.getSectionOrder());
			section.setDescription(sectionDTO.getDescription());
			Course updatedCourse = courseService.addSectionToCourse(courseId, section);
            return new ResponseEntity<>(section, HttpStatus.CREATED);

		} catch (RuntimeException e) {
			// Catch the "Course not found" exception from the service
			if (e.getMessage().contains("Course not found")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	@GetMapping("/getsections/{courseId}")
    public ResponseEntity<CourseWithContentDTO> getCourseContent(@PathVariable Long courseId) {
        try {
            CourseWithContentDTO courseContent = courseService.getCourseWithSectionsAndTopics(courseId);
            return ResponseEntity.ok(courseContent);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Course not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
	
	@DeleteMapping("/deletesection/{courseId}/{sectionId}")
	public ResponseEntity<String> deleteSection(@PathVariable Long courseId, @PathVariable Long sectionId) {
		try {
		
			courseService.deleteSectionFromCourse(courseId, sectionId); 
			return ResponseEntity.ok("Section with ID " + sectionId + " and its videos deleted successfully from course " + courseId + ".");
		} catch (RuntimeException e) {
			// Catch specific exceptions for better error handling
			if (e.getMessage().contains("Course not found")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Course not found with ID: " + courseId);
			} else if (e.getMessage().contains("Section not found")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Section not found with ID: " + sectionId + " in course " + courseId);
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting section: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
		}
	}
	@DeleteMapping("/deletetopic/course/{courseId}/section/{sectionId}/topic/{topicId}")
	public ResponseEntity<String> deleteTopic(@PathVariable Long courseId, @PathVariable Long sectionId,
			@PathVariable Long topicId) {
		try {
			
			courseService.deleteTopicFromSection(courseId, sectionId, topicId);
			return ResponseEntity.ok("Topic with ID " + topicId + " and its video deleted successfully from section "
					+ sectionId + " in course " + courseId + ".");
		} catch (RuntimeException e) {
			// Catch specific exceptions for better error handling
			if (e.getMessage().contains("Course not found")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Error: Course not found with ID: " + courseId);
			} else if (e.getMessage().contains("Section not found")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Error: Section not found with ID: " + sectionId + " in course " + courseId);
			} else if (e.getMessage().contains("Topic not found")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Topic not found with ID: " + topicId
						+ " in section " + sectionId + " of course " + courseId);
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error deleting topic: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occurred: " + e.getMessage());
		}
	}

	@PostMapping(value = "/addtopic/course/{courseId}/section/{sectionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
		public ResponseEntity<String> addTopicToSection(@PathVariable("courseId") Long courseId,
				@PathVariable("sectionId") Long sectionId, @RequestParam("title") String title,
				@RequestParam(value = "description", required = false) String description,
				@RequestParam("topicOrder") Integer topicOrder,
				@RequestParam("durationMinutes") Integer durationMinutes,
				@RequestPart("video") MultipartFile videoFile) {

			try {
				System.out.println("uploading");
				String videoUrl = s3Service.uploadFileSec(videoFile);

				Topic topic = new Topic();
				topic.setTitle(title);
				topic.setDescription(description);
				topic.setTopicOrder(topicOrder);
				topic.setDurationMinutes(durationMinutes);
				topic.setVideoUrl(videoUrl); // Set the S3 video URL

				courseService.addTopicToSection(courseId, sectionId, topic);

				return ResponseEntity.ok("Successfully added topic to section " + sectionId + " in course " + courseId);

			} catch (RuntimeException e) {
				if (e.getMessage().contains("Course not found")) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Course not found with ID: " + courseId);
				} else if (e.getMessage().contains("Section not found")) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Section not found with ID: " + sectionId + " in course " + courseId);
				}
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during topic creation: " + e.getMessage());
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing video file: " + e.getMessage());
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
			}
		}
	
}