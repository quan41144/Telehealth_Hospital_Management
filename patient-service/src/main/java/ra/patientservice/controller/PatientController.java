package ra.patientservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.patientservice.dto.request.PatientInfoRequest;
import ra.patientservice.dto.request.UpdatePatientInfoRequest;
import ra.patientservice.dto.response.ApiResponse;
import ra.patientservice.service.PatientService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> createInfo(@PathVariable Long userId, @Valid @RequestBody PatientInfoRequest patientInfoRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Tạo mới hồ sơ bệnh nhân thành công!",
                patientService.createInfo(userId, patientInfoRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.CREATED);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> getInfo(@PathVariable Long userId) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy thông tin hồ sơ bệnh nhân thành công!",
                patientService.getInfo(userId),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> updateInfo(@PathVariable Long userId, @RequestBody UpdatePatientInfoRequest updatePatientInfoRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhật thông tin hồ sơ bệnh nhân thành công!",
                patientService.updateInfo(userId, updatePatientInfoRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
}
