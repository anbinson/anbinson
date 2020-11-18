package com.tsb.ods.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import com.tsb.ods.model.TransactionEntity;

@Repository
public interface TransactionRepository extends CassandraRepository<TransactionEntity, String> {
    @Query("insert into table odsdb.kc03(trans_id, name, balance) values(?0, ?1, ?2")
    void addTransaction(String trans_id, String name, String balance);
}
