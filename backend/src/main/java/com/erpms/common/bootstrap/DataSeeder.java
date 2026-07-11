package com.erpms.common.bootstrap;

import com.erpms.department.entity.Department;
import com.erpms.department.repository.DepartmentRepository;
import com.erpms.user.entity.UserAccount;
import com.erpms.user.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * One-shot bootstrap that guarantees an administrator account exists and
 * seeds a small catalog of default departments the first time the platform
 * boots.
 *
 * <p>Idempotent: safe to run on every startup.
 */
@Component
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserAccountRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;
    private final String adminFullName;

    public DataSeeder(
            UserAccountRepository userRepository,
            DepartmentRepository departmentRepository,
            PasswordEncoder passwordEncoder,
            @Value("${erpms.bootstrap.admin-email:admin@example.com}") String adminEmail,
            @Value("${erpms.bootstrap.admin-password:Admin12345}") String adminPassword,
            @Value("${erpms.bootstrap.admin-full-name:Platform Administrator}") String adminFullName
    ) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminFullName = adminFullName;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seed() {
        seedAdmin();
        seedDepartments();
    }

    private void seedAdmin() {
        if (userRepository.existsByEmailIgnoreCase(adminEmail)) {
            log.info("[seed] admin account '{}' already present — skipping", adminEmail);
            return;
        }
        UserAccount admin = new UserAccount();
        admin.setEmail(adminEmail.trim().toLowerCase());
        admin.setFullName(adminFullName);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole("ADMINISTRATOR");
        admin.setStatus("ACTIVE");
        userRepository.save(admin);
        log.info("[seed] created admin account '{}'", adminEmail);
    }

    private void seedDepartments() {
        seedDepartment("R&D", "Research and Development",
                "Core research programmes across all scientific disciplines");
        seedDepartment("ENG", "Engineering",
                "Systems, mechanical, electrical and software engineering");
        seedDepartment("OPS", "Laboratory Operations",
                "Day-to-day operation of laboratories and shared facilities");
        seedDepartment("FIN", "Finance",
                "Budgets, expenses and procurement approvals");
        seedDepartment("QA", "Quality & Compliance",
                "Standards, audits and regulatory compliance");
    }

    private void seedDepartment(String code, String name, String description) {
        departmentRepository.findByCodeIgnoreCase(code).ifPresentOrElse(
                d -> {},
                () -> {
                    Department d = new Department();
                    d.setCode(code);
                    d.setName(name);
                    d.setDescription(description);
                    d.setActive(true);
                    departmentRepository.save(d);
                    log.info("[seed] created department {} — {}", code, name);
                }
        );
    }
}
