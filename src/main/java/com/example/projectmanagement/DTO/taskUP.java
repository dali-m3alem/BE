
package com.example.projectmanagement.DTO;

        import lombok.AllArgsConstructor;
        import lombok.Builder;
        import lombok.Data;
        import lombok.NoArgsConstructor;

        import java.time.LocalDateTime;
        import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class taskUP {
    private Long id;
    private String title;
    private Date dueDate;
    private String description;
    private String email;
    private Long manager;

    private Long activity;
}

