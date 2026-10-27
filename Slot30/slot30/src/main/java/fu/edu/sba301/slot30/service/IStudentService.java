package fu.edu.sba301.slot30.service;

import fu.edu.sba301.slot30.entity.Student;

import java.util.List;

public interface IStudentService {
    Student create(Student student);
    List<Student> getAll();
    Student getById(Long id);
    Student update(Student student);
    Student updateMarks(Long id, double marks);
    void delete(Long id);
}
