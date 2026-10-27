package fu.edu.sba301.slot28;

import fu.edu.sba301.slot28.app.WorkbookRunner;
import fu.edu.sba301.slot28.util.HibernateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Sba301Slot28Application implements CommandLineRunner {
    @Value("${lab.experiment:help}")
    private String experiment;

    @Value("${lab.student-id:1}")
    private long studentId;

    @Value("${lab.batch-size:1000}")
    private int batchSize;

    public static void main(String[] args) {
        SpringApplication.run(Sba301Slot28Application.class, args);
    }

    @Override
    public void run(String... args) {
        if ("help".equalsIgnoreCase(experiment)) {
            printHelp();
            return;
        }

        try {
            new WorkbookRunner(HibernateUtil.getSessionFactory())
                    .run(experiment, studentId, batchSize);
        } finally {
            HibernateUtil.shutdown();
        }
    }

    private static void printHelp() {
        System.out.println("SBA301 Slot 28 - Hibernate Workbook");
        System.out.println("Set DB_PASSWORD, then select one experiment:");
        System.out.println("  lifecycle | dirty-checking | first-level-cache | detached-merge");
        System.out.println("  hql | relationship | rollback | break-hql | batch");
        System.out.println("Example: mvn spring-boot:run -Dspring-boot.run.arguments=\"--lab.experiment=lifecycle\"");
    }
}
