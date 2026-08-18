package ra.authservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ra.authservice.common.RoleType;
import ra.authservice.entity.Role;
import ra.authservice.repository.RoleRepository;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String @NonNull ... args) {
        List<RoleType> enumRoles = Arrays.asList(RoleType.values());
        List<Role> dbRoles = roleRepository.findAll();

        for (Role dbRole : dbRoles) {
            if (enumRoles.contains(dbRole.getRoleName())) {
                try {
                    roleRepository.delete(dbRole);
                    log.info("Role {} đã được xoá!", dbRole.getRoleName());
                }
                catch (Exception e) {
                    log.warn("Không thể xoá role {}, vì đang có user sở hữu role này!", dbRole.getRoleName());
                }
            }
        }
        for (RoleType roleType : enumRoles) {
            if (roleRepository.findByRoleName(roleType).isEmpty()) {
                Role role = new Role();
                role.setRoleName(roleType);
                roleRepository.save(role);
                log.info("Đã thêm role mới {}", roleType);
            }
        }
    }
}
