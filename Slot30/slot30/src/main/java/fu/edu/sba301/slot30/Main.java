package fu.edu.sba301.slot30;

import fu.edu.sba301.slot30.dao.StudentDAO;
import fu.edu.sba301.slot30.entity.Student;
import fu.edu.sba301.slot30.repository.IStudentRepository;
import fu.edu.sba301.slot30.repository.StudentRepository;
import fu.edu.sba301.slot30.service.IStudentService;
import fu.edu.sba301.slot30.service.StudentService;
import fu.edu.sba301.slot30.util.HibernateUtil;
import org.hibernate.SessionFactory;

public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try {
            StudentDAO dao = new StudentDAO(sessionFactory);
            IStudentRepository repository = new StudentRepository(dao);
            IStudentService service = new StudentService(repository);

            System.out.println("=== SBA301 Slot 30: Hibernate CRUD Application Layers ===");

            // 1. CREATE
            Student s1 = service.create(new Student("an.nguyen@fpt.edu.vn", "An", "Nguyen", 8.5));
            System.out.println("Created Student: id=" + s1.getId() + ", name=" + s1.getFirstName() + " " + s1.getLastName());

            Student s2 = service.create(new Student("binh.tran@fpt.edu.vn", "Binh", "Tran", 7.0));
            System.out.println("Created Student: id=" + s2.getId() + ", name=" + s2.getFirstName() + " " + s2.getLastName());

            // 2. READ ALL
            System.out.println("All students count: " + service.getAll().size());

            // 3. READ ONE
            Student found = service.getById(s1.getId());
            System.out.println("Found student by id: " + found.getEmail());

            // 4. UPDATE (dirty checking)
            service.updateMarks(s1.getId(), 9.2);
            System.out.println("Updated marks for " + s1.getId() + " to 9.2");

            // 5. DELETE
            service.delete(s2.getId());
            System.out.println("Deleted student id=" + s2.getId());
            System.out.println("Remaining students: " + service.getAll().size());

            System.out.println("=== Demo finished successfully ===");
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
