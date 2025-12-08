package ru.ssau.tk._shederu_._lab1_.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.ssau.tk._shederu_._lab1_.entities.RoleEntity;
import ru.ssau.tk._shederu_._lab1_.entities.UserEntity;
import ru.ssau.tk._shederu_._lab1_.repository.RoleRepository;
import ru.ssau.tk._shederu_._lab1_.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            roleRepository.save(new RoleEntity("ADMIN"));
            System.out.println("✓ Role ADMIN created");
        }
        if (roleRepository.findByName("CREATOR").isEmpty()) {
            roleRepository.save(new RoleEntity("CREATOR"));
            System.out.println("✓ Role CREATOR created");
        }
        if (roleRepository.findByName("VIEWER").isEmpty()) {
            roleRepository.save(new RoleEntity("VIEWER"));
            System.out.println("✓ Role VIEWER created");
        }

        if (userRepository.findByLogin("admin").isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setLogin("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));

            Set<RoleEntity> roles = new HashSet<>();
            roles.add(roleRepository.findByName("ADMIN").get());
            admin.setRoles(roles);

            userRepository.save(admin);
            System.out.println("✓ ADMIN user created: login=admin, password=admin123");
        }
    }
}
