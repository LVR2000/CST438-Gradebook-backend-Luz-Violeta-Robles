package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentListDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;

@SpringBootTest
public class EndToEndTestCreateAssignment {

	public static final String CHROME_DRIVER_FILE_LOCATION = "C:\\Users\\atelo\\Downloads\\chromedriver_win32\\chromedriver.exe";

	public static final String URL = "http://localhost:3000/createAssignment";
	public static final String TEST_USER_EMAIL = "test@csumb.edu";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	public static final String TEST_ASSIGNMENT_NAME = "Test Assignment";
	public static final int TEST_COURSE = 99999;
	public static final String TEST_DUE_DATE = "2020-10-20";
	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentGradeRepository assignnmentGradeRepository;

	@Autowired
	AssignmentRepository assignmentRepository;

	@Test
	public void addAssignmentTest() throws Exception {

//		Database setup:  create course		
		Course c = new Course();
		c.setCourse_id(99999);
		c.setInstructor(TEST_INSTRUCTOR_EMAIL);
		c.setSemester("Fall");
		c.setYear(2021);
		c.setTitle("Test Assignment");

		courseRepository.save(c);

		List<Assignment> ag = null;

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);
		
		
		WebElement we;
	try {
		
		driver.get(URL);
		// must have a short wait to allow time for the page to download 
		Thread.sleep(SLEEP_DURATION);
		
		// enter an assignment name
		we = driver.findElement(By.id("name"));
		we.sendKeys(TEST_ASSIGNMENT_NAME);
		
		// enter an assignment due date
		we = driver.findElement(By.id("dueDate"));
		we.sendKeys(TEST_DUE_DATE.toString());
		
		// enter a COURSE
		we = driver.findElement(By.id("course"));
		we.sendKeys(String.valueOf(TEST_COURSE));
		
		// find and click the submit button
		we = driver.findElement(By.id("submit"));
		we.click();
		Thread.sleep(SLEEP_DURATION);

		// verify that assignment has been added to the database
		ag = (List<Assignment>) assignmentRepository.findAll();
		int size = ag.size()-1;
		assertEquals(TEST_ASSIGNMENT_NAME, ag.get(size).getName());
		assertEquals(TEST_DUE_DATE.toString(), ag.get(size).getDueDate().toString());
		assertEquals(TEST_COURSE, ag.get(size).getCourse().getCourse_id());

	} catch (Exception ex) {
		throw ex;
	} finally {

		/*
		 *  clean up database so the test is repeatable.
		 */
		System.out.print(ag);
		assignmentRepository.delete(ag.get(ag.size()-1));

		courseRepository.delete(c);

		driver.quit();
	}
   }
}
		