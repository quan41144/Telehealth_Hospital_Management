package ra.patientservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.patientservice.entity.Patient;
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
