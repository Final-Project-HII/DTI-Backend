package com.hii.finalProject.stockMutationJournal.repository;

import com.hii.finalProject.order.entity.Order;
import com.hii.finalProject.products.entity.Product;
import com.hii.finalProject.stockMutationJournal.entity.StockMutationJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMutationJournalRepository extends JpaRepository<StockMutationJournal, Long>, JpaSpecificationExecutor<StockMutationJournal> {
    List<StockMutationJournal> findByOrder(Order order);
    List<StockMutationJournal> findByProduct(Product product);
}


