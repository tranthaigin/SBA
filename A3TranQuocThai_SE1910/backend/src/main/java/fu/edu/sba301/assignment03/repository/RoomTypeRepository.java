/**
 * @author Trần Quốc Thái
 * Class: SE1910
 * Student ID: CE190243
 */
package fu.edu.sba301.assignment03.repository;

import fu.edu.sba301.assignment03.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {
}
