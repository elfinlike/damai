package com.bonss.system.domain.vo;

import com.bonss.system.domain.ConsumableType;
import com.bonss.system.domain.Manual;
import com.bonss.system.domain.ProductModel;
import com.bonss.system.domain.Tutorial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelVO extends ProductModel {

    /**
     *
     */
    private List<ConsumableType> consumables;

    private List<Tutorial> tutorials;

    private List<Manual> manuals;

}
