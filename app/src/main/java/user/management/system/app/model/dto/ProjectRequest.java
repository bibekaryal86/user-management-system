package user.management.system.app.model.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {
  private String name;
  private String description;
  private int statusId;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private String repo;
  private String link;
}