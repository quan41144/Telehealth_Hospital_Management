package ra.patientservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ra.patientservice.repository.PatientRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventListener {
    private final PatientRepository patientRepository;
    @RabbitListener(queues = "user_delete_queue")
    public void handleUserDeletedEvent(Long userId) {
        log.info("Nhận thông báo xóa User ID: {}", userId);
        if (patientRepository.existsById(userId)) {
            patientRepository.deleteById(userId);
            log.info("Đã xóa hoàn toàn hỗ sơ bệnh nhân ID: {}", userId);
        }
        else {
            log.info("Không tìm thấy hồ sơ bệnh nhân ID: {}", userId);
        }
    }
}
