package react.moneymanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import react.moneymanager.dto.AuthDTO;
import react.moneymanager.dto.ProfileDTO;
import react.moneymanager.entity.ProfileEntity;
import react.moneymanager.repository.ProfileRepository;
import react.moneymanager.util.JwtUtil;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public ProfileDTO registerProfile(ProfileDTO ProfileDTO) {
        ProfileEntity newProfile = toEntity(ProfileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);
        // send activation email
        String activationLink = "http://localhost:8080/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate your money manager account";
        String body = "Click on the following link to activate your account, " + activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject, body);
        return toDto(newProfile);
    }

    public ProfileEntity toEntity(ProfileDTO ProfileDTO) {
        return ProfileEntity.builder()
                .id(ProfileDTO.getId())
                .fullName(ProfileDTO.getFullName())
                .email(ProfileDTO.getEmail())
                .password(passwordEncoder.encode(ProfileDTO.getPassword()))
                .profileImagUrl(ProfileDTO.getProfileImagUrl())
                .createdAt(ProfileDTO.getCreatedAt())
                .updatedAt(ProfileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDto(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .password(profileEntity.getPassword())
                .profileImagUrl(profileEntity.getProfileImagUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDto(ProfileEntity profileEntity, boolean isPublic) {

        ProfileDTO.ProfileDTOBuilder ProfileDTOBuilder = ProfileDTO.builder()
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImagUrl(profileEntity.getProfileImagUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt());
        if (!isPublic) {
            ProfileDTOBuilder.password(profileEntity.getPassword()).id(profileEntity.getId());
        }

        return ProfileDTOBuilder.build();
    }


    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken).map(
                profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                }).orElse(false);
    }

    public boolean isAccountActive(String email) {
        return profileRepository.findByEmail(email).map(ProfileEntity::getIsActive).orElse(false);
    }

    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new UsernameNotFoundException("User not found with the email + " + authentication.getName())
        );
    }

    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity currentUser = null;
        if (email == null) {
            currentUser = getCurrentProfile();
        } else {
            currentUser = profileRepository.findByEmail(email).orElseThrow(
                    () -> new UsernameNotFoundException("User not found with the email + " + email)
            );
        }
        return toDto(currentUser, true);
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
            // Generate JWT Token
            String token = jwtUtil.generateToken(authDTO.getEmail());
            return Map.of(
                    "token", token,
                    "user", getPublicProfile(authDTO.getEmail()));
        } catch (Exception e) {
               throw new RuntimeException("Invalid email or password!");
        }
    }

}
