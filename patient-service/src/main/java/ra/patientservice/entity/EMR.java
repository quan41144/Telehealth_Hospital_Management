package ra.patientservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.patientservice.security.crypt.CryptoConverter;

import java.time.LocalDateTime;

@Entity
@Table(name = "emr_records")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EMR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emr_record_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;
    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;
    @Column(name = "visit_date", nullable = false)
    @Builder.Default
    private LocalDateTime visitDate = LocalDateTime.now();
    @Convert(converter = CryptoConverter.class)
    @Column(columnDefinition = "text")
    private String symptoms;
    @Convert(converter = CryptoConverter.class)
    @Column(columnDefinition = "text")
    private String diagnosis;
    @Convert(converter = CryptoConverter.class)
    @Column(columnDefinition = "text")
    private String prescription;
}
