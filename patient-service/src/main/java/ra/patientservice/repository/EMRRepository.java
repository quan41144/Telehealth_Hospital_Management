package ra.patientservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.patientservice.entity.EMR;
@Repository
public interface EMRRepository extends JpaRepository<EMR, Long> {
}
