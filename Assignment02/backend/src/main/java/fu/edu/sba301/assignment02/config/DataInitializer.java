package fu.edu.sba301.assignment02.config;

import fu.edu.sba301.assignment02.entity.Category;
import fu.edu.sba301.assignment02.entity.NewsArticle;
import fu.edu.sba301.assignment02.entity.SystemAccount;
import fu.edu.sba301.assignment02.entity.Tag;
import fu.edu.sba301.assignment02.repository.CategoryRepository;
import fu.edu.sba301.assignment02.repository.NewsArticleRepository;
import fu.edu.sba301.assignment02.repository.SystemAccountRepository;
import fu.edu.sba301.assignment02.repository.TagRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Set;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final SystemAccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final NewsArticleRepository newsArticleRepository;

    public DataInitializer(SystemAccountRepository accountRepository,
                           CategoryRepository categoryRepository,
                           TagRepository tagRepository,
                           NewsArticleRepository newsArticleRepository) {
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.newsArticleRepository = newsArticleRepository;
    }

    @Override
    public void run(String... args) {
        if (accountRepository.count() == 0) {
            SystemAccount admin = new SystemAccount((short) 1, "System Admin", "admin@funews.com", 1, "@1");
            SystemAccount staff = new SystemAccount((short) 2, "Staff Member", "staff@funews.com", 2, "@1");
            accountRepository.save(admin);
            accountRepository.save(staff);
        }

        if (tagRepository.count() == 0) {
            tagRepository.save(new Tag(1, "AI", "Artificial Intelligence & ML"));
            tagRepository.save(new Tag(2, "Cloud", "Cloud Computing & DevOps"));
            tagRepository.save(new Tag(3, "Education", "Higher Education & Research"));
        }

        if (categoryRepository.count() == 0) {
            Category cat1 = new Category(null, "Technology & Science", "Latest innovations in software and hardware", null, true);
            Category cat2 = new Category(null, "Campus Life & Academics", "Events, academic achievements and sports", null, true);
            categoryRepository.save(cat1);
            categoryRepository.save(cat2);

            if (newsArticleRepository.count() == 0) {
                NewsArticle na1 = new NewsArticle();
                na1.setNewsArticleId("NA0001");
                na1.setNewsTitle("FPT University launches AI Innovation Hub");
                na1.setHeadline("Breakthrough center for AI and Robotics opened today");
                na1.setCreatedDate(LocalDateTime.now());
                na1.setNewsContent("The hub connects university researchers with top technology corporations.");
                na1.setNewsSource("FPT University Newsroom");
                na1.setCategory(cat1);
                na1.setNewsStatus(true);
                na1.setCreatedById((short) 2);
                na1.setTags(Set.of(tagRepository.findById(1).orElseThrow()));
                newsArticleRepository.save(na1);
            }
        }
    }
}
