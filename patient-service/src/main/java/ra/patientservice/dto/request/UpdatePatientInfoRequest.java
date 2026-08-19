package ra.patientservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdatePatientInfoRequest {
    private String fullName;
    private String gender;
    private String address;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dob;
    private String bloodType;
    private String identityNumber;
}
