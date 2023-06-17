package com.project.Collok;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;

@RestController
@RequestMapping("/api")
class StudentController {
    private Connection connection;
    private final String DB_URL = "jdbc:sqlite:authorization.db";

    // Метод для установки соединения с базой данных
    private void connect() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
    }

    // Метод для закрытия соединения с базой данных
    private void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public StudentController() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS student (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name VARCHAR(50) UNIQUE NOT NULL, " +
                    "age INTEGER NOT NULL, " +
                    "specification VARCHAR(255) NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            stmt.execute("CREATE TABLE IF NOT EXISTS grades (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "student_id INTEGER NOT NULL, " +
                    "grade INTEGER NOT NULL, " +
                    "subject VARCHAR(255) NOT NULL, " +
                    "FOREIGN KEY (student_id) REFERENCES student (id) ON DELETE CASCADE ON UPDATE CASCADE)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/students")
    public ResponseEntity<String> addStudent(@RequestBody Student student) {

        try {
            connect();

            String query = "INSERT INTO student (name, age, specification) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, student.getName());
            statement.setInt(2, student.getAge());
            statement.setString(3, student.getSpecification());
            statement.executeUpdate();

            disconnect();

            return ResponseEntity.status(HttpStatus.CREATED).body("Student added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register student");
        }
    }

    @GetMapping("/students")
    public ResponseEntity<String> getAllStudents() {
        try {
            connect();

            String query = "SELECT * FROM student";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            StringBuilder response = new StringBuilder();
            while (rs.next()) {
                String name = rs.getString("name");
                String age = rs.getString("age");
                String specification = rs.getString("specification");
                response.append("Name: ").append(name).append(", Age: ").append(age).append(", Specification: ").append(specification).append("\n");
            }

            disconnect();

            return ResponseEntity.ok(response.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve students");
        }

    }



    @PostMapping("/grades")
    public ResponseEntity<String> addGrade(@RequestBody Grade grade) {
        try {
            connect();

            String studentCheckQuery = "SELECT COUNT(*) FROM student WHERE id = ?";
            PreparedStatement studentCheckStatement = connection.prepareStatement(studentCheckQuery);
            studentCheckStatement.setInt(1, grade.getStudentId());
            ResultSet studentCheckResult = studentCheckStatement.executeQuery();
            if (studentCheckResult.getInt(1) == 0) {
                disconnect();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Student with the specified ID does not exist");
            }

            String query = "INSERT INTO grades (student_id, subject, grade) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, grade.getStudentId());
            statement.setString(2, grade.getSubject());
            statement.setInt(3, grade.getGrade());
            statement.executeUpdate();

            disconnect();

            return ResponseEntity.status(HttpStatus.CREATED).body("Grade added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add grade");
        }
    }
    @GetMapping("/grades/{studentId}")
    public ResponseEntity<String> getGradesByStudent(@PathVariable int studentId) {
        try {
            connect();

            String query = "SELECT * FROM grades WHERE student_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, studentId);
            ResultSet rs = statement.executeQuery();

            StringBuilder response = new StringBuilder();
            while (rs.next()) {
                String subject = rs.getString("subject");
                int grade = rs.getInt("grade");
                response.append("Subject: ").append(subject).append(", Grade: ").append(grade).append("\n");
            }

            disconnect();

            return ResponseEntity.ok(response.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve grades");
        }
    }
}