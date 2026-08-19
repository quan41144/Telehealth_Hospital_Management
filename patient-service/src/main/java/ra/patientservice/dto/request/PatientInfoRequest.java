package ra.patientservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PatientInfoRequest {
    @NotBlank(message = "Không được để trống họ và tên!")
    private String fullName;
    @NotBlank(message = "Không được để trống giới tính!")
    private String gender;
    @NotBlank(message = "Không được để trống địa chỉ!")
    private String address;
    @NotNull(message = "Không được để trống ngày sinh!")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dob;
    private String bloodType;
    @NotBlank(message = "Không được để trống căn cước công dân!")
    private String identityNumber;
}
