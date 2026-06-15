package meow.at.bms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TheatreDto {

    private Long id;
    private String name;
    private String address;
    private String city;
    private Integer totalScreens;
}
