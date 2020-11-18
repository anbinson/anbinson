package com.tsb.ods.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "kc03")
@Data
@Builder
public class TransactionEntity {
    @PrimaryKey("trans_id")
    private String transId;

    @Column("name")
    private String name;
    @Column("balance")
    private double balance;
}
