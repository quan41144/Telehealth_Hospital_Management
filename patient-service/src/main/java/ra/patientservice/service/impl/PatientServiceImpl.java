package ra.patientservice.service.impl;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.patientservice.client.AuthServiceClient;
import ra.patientservice.dto.request.PatientInfoRequest;
import ra.patientservice.dto.request.UpdatePatientInfoRequest;
import ra.patientservice.dto.response.AuthInfoResponse;
import ra.patientservice.dto.response.PatientInfoResponse;
import ra.patientservice.entity.Patient;
import ra.patientservice.exception.BadRequestException;
import ra.patientservice.exception.ConflictException;
import ra.patientservice.exception.ResourceNotFoundException;
import ra.patientservice.repository.PatientRepository;
import ra.patientservice.service.PatientService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final AuthServiceClient authServiceClient;

    @Override
    public AuthInfoResponse verifyAndGetAuthInfo(Long userId) {
        try {
            return authServiceClient.getUserById(userId);
        }
        catch (FeignException.NotFound ex) {
            if (patientRepository.existsById(userId)) {
                patientRepository.deleteById(userId);
                log.info("********** Đã đồng bộ dữ liệu hồ sơ bệnh nhân!**********");
            }
            throw new ResourceNotFoundException("Tài khoản không tồn tại!");
        }
        catch (FeignException.BadRequest ex) {
            throw new BadRequestException("Tài khoản đã bị vô hiệu hóa!");
        }
        catch (FeignException ex) {
            log.error("Lỗi khi giao tiếp với Auth Service: ", ex);
            throw new RuntimeException("Lỗi hệ thống!");
        }
    }

    @Override
    @Transactional
    @Caching(
            evict = {@CacheEvict(value = "allPatients", allEntries = true)},
            put = {@CachePut(value = "patientInfo", key = "#userId")}
    )
    public PatientInfoResponse createInfo(Long userId, PatientInfoRequest patientInfoRequest) {
        AuthInfoResponse authInfoResponse = verifyAndGetAuthInfo(userId);
        if (patientRepository.existsById(userId)) {
            throw new ConflictException("Hồ sơ bệnh nhân đã tồn tại!");
        }
        Patient patient = Patient.builder()
                .id(userId)
                .fullName(patientInfoRequest.getFullName())
                .gender(patientInfoRequest.getGender())
                .address(patientInfoRequest.getAddress())
                .dob(patientInfoRequest.getDob())
                .bloodType(patientInfoRequest.getBloodType())
                .identityNumber(patientInfoRequest.getIdentityNumber())
                .build();
        patientRepository.save(patient);
        log.info("************** Tạo mới hồ sơ thành công ***************");
        return PatientInfoResponse.builder()
                .patientId(patient.getId())
                .fullName(patient.getFullName())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .dob(patient.getDob())
                .identityNumber(patient.getIdentityNumber())
                .email(authInfoResponse != null ? authInfoResponse.getEmail() : null)
                .phone(authInfoResponse != null ? authInfoResponse.getPhone() : null)
                .bloodType(patient.getBloodType())
                .build();
    }

    @Override
    @Cacheable(value = "patientInfo", key = "#userId")
    public PatientInfoResponse getInfo(Long userId) {
        AuthInfoResponse authInfoResponse = verifyAndGetAuthInfo(userId);
        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chưa hoàn thiện hồ sơ bệnh nhân!"));
        return PatientInfoResponse.builder()
                .patientId(patient.getId())
                .fullName(patient.getFullName())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .dob(patient.getDob())
                .identityNumber(patient.getIdentityNumber())
                .email(authInfoResponse != null ? authInfoResponse.getEmail() : null)
                .phone(authInfoResponse != null ? authInfoResponse.getPhone() : null)
                .bloodType(patient.getBloodType())
                .build();
    }

    @Override
    @Transactional
    @Caching(
            put = { @CachePut(value = "patientInfo", key = "#userId") },
            evict = { @CacheEvict(value = "allPatients", allEntries = true) }
    )
    public PatientInfoResponse updateInfo(Long userId, UpdatePatientInfoRequest updatePatientInfoRequest) {
        AuthInfoResponse authInfoResponse = verifyAndGetAuthInfo(userId);
        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chưa hoàn thiện hồ sơ bệnh nhân!"));
        if (updatePatientInfoRequest.getFullName() != null) {
            patient.setFullName(updatePatientInfoRequest.getFullName());
        }
        if (updatePatientInfoRequest.getGender() != null) {
            patient.setGender(updatePatientInfoRequest.getGender());
        }
        if (updatePatientInfoRequest.getAddress() != null) {
            patient.setAddress(updatePatientInfoRequest.getAddress());
        }
        if (updatePatientInfoRequest.getDob() != null) {
            patient.setDob(updatePatientInfoRequest.getDob());
        }
        if (updatePatientInfoRequest.getBloodType() != null) {
            patient.setBloodType(updatePatientInfoRequest.getBloodType());
        }
        if (updatePatientInfoRequest.getIdentityNumber() != null) {
            patient.setIdentityNumber(updatePatientInfoRequest.getIdentityNumber());
        }
        patientRepository.save(patient);
        log.info("**************Cập nhật hồ sơ thành công!****************");
        return PatientInfoResponse.builder()
                .patientId(userId)
                .fullName(patient.getFullName())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .dob(patient.getDob())
                .identityNumber(patient.getIdentityNumber())
                .email(authInfoResponse != null ? authInfoResponse.getEmail() : null)
                .phone(authInfoResponse != null ? authInfoResponse.getPhone() : null)
                .bloodType(patient.getBloodType())
                .build();
    }
}
