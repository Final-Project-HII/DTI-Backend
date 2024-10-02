package com.hii.finalProject.stockMutation.dto;

import com.hii.finalProject.stockMutationJournal.entity.StockMutationJournal;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockMutationJournalDto {
    private Long id;
    private Long UUID;
    private Long stockMutationId;
    private String productName;
    private String warehouseName;
    private String anotherWarehouse;
    private Integer beginningStock;
    private Integer endingStock;
    private StockMutationJournal.MutationType mutationType;
    private Integer quantity;
    private LocalDateTime createdAt;
    private Long loginWarehouseId;

}
