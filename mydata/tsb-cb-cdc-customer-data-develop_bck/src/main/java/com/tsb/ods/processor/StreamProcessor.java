package com.tsb.ods.processor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.tsb.ods.model.TransactionEntity;
import com.tsb.ods.repository.TransactionRepository;
import com.tsb.ods.stream.schema.avro.kc03.KC03;

import java.util.function.Consumer;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreamProcessor {

    private final TransactionRepository transactionRepository;
    private final MeterRegistry meterRegistry;
    
    private Counter transactionsInCounter; 
    private Counter transactionsOutCounter; 

    @PostConstruct
    public void init() {
    	log.info("Create and register metric counters with Meter registry");
    	transactionsInCounter = Counter.builder("tsb_ods_in_transactions_in_total")
    								.description("Total number of transactions since process start")
    								.register(meterRegistry);
    	transactionsOutCounter = Counter.builder("tsb_ods_out_transactions_in_total")
									.description("Total number of transactions processed")
									.register(meterRegistry);
    	
    }
    

    /**
     * Consume Transactions stream (from ods) and store it in scylladb
     * 
     */
    @Bean
    public Consumer<KTable<String, KC03>> processTransactionsStream() {
        return input -> input
                .toStream()
                // filter (and trace) messages
                .filter((k, transaction) -> {
                    log.debug("Received a message from DeviceByDASUser");
                    transactionsInCounter.increment();
                    return transaction != null &&
                            StringUtils.isNotBlank(transaction.getTransId());
                })
                .foreach((k, transaction) -> {
                    // store the customer in db
                    transactionRepository.save(TransactionEntity.builder()
                            .transId(transaction.getTransId())
                            .name(transaction.getName())
                            .balance(transaction.getBalance())
                    		.build());
                    log.debug("Transaction stored in Scylladb");
                    transactionsOutCounter.increment();
                });
    }
}
