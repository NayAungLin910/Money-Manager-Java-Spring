package react.moneymanager.dto;

import jdk.jshell.Snippet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDto {
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String profileImagUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
