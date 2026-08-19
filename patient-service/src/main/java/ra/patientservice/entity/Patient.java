package ra.patientservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.patientservice.security.crypt.CryptoConverter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "patients")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Patient {
    @Id
    @Column(name = "patient_id")
    private Long id;
    @Column(name = "full_name", length = 200, nullable = false)
    private String fullName;
    @Column(length = 100, nullable = false)
    private String gender;
    @Column(nullable = false, columnDefinition = "text")
    private String address;
    @Column(nullable = false)
    private LocalDate dob;
    @Column(name = "blood_type", length = 20)
    private String bloodType;
    @Convert(converter = CryptoConverter.class)
    private String identityNumber;
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<EMR> medicalRecords;
}
