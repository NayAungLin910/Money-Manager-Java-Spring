package react.moneymanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import react.moneymanager.entity.ProfileEntity;
import react.moneymanager.repository.ProfileRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final ProfileRepository profileRepository;
    private final ExpenseService expenseService;
    private final EmailService emailService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 22 * * *", zone = "IST")
    public void sendDailyIncomeExpenseReminder() {
        log.info("Job started: sendDailyIncomeExpenseReminder()");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for(ProfileEntity profile: profiles) {
            String body = "Hi " + profile.getFullName() + "<br><br>"
                    + "This is a friendly reminder to add your income and expenses for today in Money Manager.<br><br>";
        }
    }
}
