package com.bonss.system.domain.DTO;

import com.bonss.system.domain.ProductModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelDTO extends ProductModel {

    private List<Long> consumableIds;

    private List<Long> manualIds;

    private List<Long> tutorialIds;

}
