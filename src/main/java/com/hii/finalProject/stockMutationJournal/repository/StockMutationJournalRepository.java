package com.hii.finalProject.stockMutationJournal.repository;

import com.hii.finalProject.stockMutationJournal.entity.StockMutationJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMutationJournalRepository extends JpaRepository <StockMutationJournal, Long>, JpaSpecificationExecutor<StockMutationJournal> {
}
