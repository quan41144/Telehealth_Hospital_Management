package ra.patientservice.service;

import ra.patientservice.dto.request.PatientInfoRequest;
import ra.patientservice.dto.request.UpdatePatientInfoRequest;
import ra.patientservice.dto.response.AuthInfoResponse;
import ra.patientservice.dto.response.PatientInfoResponse;

public interface PatientService {
    AuthInfoResponse verifyAndGetAuthInfo(Long userId);
    PatientInfoResponse createInfo(Long userId, PatientInfoRequest patientInfoRequest);
    PatientInfoResponse getInfo(Long userId);
    PatientInfoResponse updateInfo(Long userId, UpdatePatientInfoRequest updatePatientInfoRequest);
}
