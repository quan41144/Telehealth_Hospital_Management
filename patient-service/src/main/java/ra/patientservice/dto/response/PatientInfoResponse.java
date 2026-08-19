package ra.patientservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PatientInfoResponse {
    private Long patientId;
    private String fullName;
    private String gender;
    private String address;
    private LocalDate dob;
    private String identityNumber;
    private String email;
    private String phone;
    private String bloodType;
}
