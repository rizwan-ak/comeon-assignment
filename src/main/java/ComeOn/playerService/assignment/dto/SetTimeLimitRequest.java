package comeon.playerservice.assignment.dto;

import lombok.Data;

@Data
public class SetTimeLimitRequest {
    private String email;
    private Integer dailyLimitMinutes;
}
