package com.growus.econnect.dto.plant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlantTypeDTO {
    private String type;
    private String cntntsNo;
    private String speclmanageInfo;

    public PlantTypeDTO(String type, String cntntsNo) {
        this.type = type;
        this.cntntsNo = cntntsNo;
    }
}
