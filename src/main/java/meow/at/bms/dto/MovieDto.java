package meow.at.bms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto {

    private Long id;
    private String title;
    private String description;
    private String language;
    private String genre;
    private String releaseDate;
    private Integer durationMins;
    private String posterUrl;
}
