package fu.edu.sba301.slot30.service;

import fu.edu.sba301.slot30.entity.Student;
import fu.edu.sba301.slot30.repository.IStudentRepository;

import java.util.List;

public class StudentService implements IStudentService {
    private final IStudentRepository repository;

    public StudentService(IStudentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Student create(Student student) {
        validate(student);
        repository.findByEmail(student.getEmail()).ifPresent(existing -> {
            throw new IllegalStateException("Email already exists: " + student.getEmail());
        });
        return repository.save(student);
    }

    @Override
    public List<Student> getAll() {
        return repository.findAll();
    }

    @Override
    public Student getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + id));
    }

    @Override
    public Student update(Student student) {
        validate(student);
        if (student.getId() == null) {
            throw new IllegalArgumentException("Student ID cannot be null for update");
        }
        repository.findById(student.getId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + student.getId()));
        return repository.update(student);
    }

    @Override
    public Student updateMarks(Long id, double marks) {
        if (marks < 0.0 || marks > 10.0) {
            throw new IllegalArgumentException("Marks must be between 0.0 and 10.0");
        }
        return repository.updateMarks(id, marks);
    }

    @Override
    public void delete(Long id) {
        boolean removed = repository.delete(id);
        if (!removed) {
            throw new IllegalArgumentException("Cannot delete: Student not found with id: " + id);
        }
    }

    private void validate(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!student.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (student.getFirstName() == null || student.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (student.getLastName() == null || student.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (student.getMarks() < 0.0 || student.getMarks() > 10.0) {
            throw new IllegalArgumentException("Marks must be between 0.0 and 10.0");
        }
    }
}
