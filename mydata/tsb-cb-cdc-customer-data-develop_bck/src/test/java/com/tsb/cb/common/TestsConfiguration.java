package com.tsb.cb.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;

@TestConfiguration
public class TestsConfiguration {
    @Bean
    EmbeddedKafkaBroker embeddedKafkaBroker(EmbeddedKafkaRule embeddedKafkaRule) {
        EmbeddedKafkaBroker embeddedKafka = embeddedKafkaRule.getEmbeddedKafka();

        return embeddedKafka;
    }

    @Bean
    EmbeddedKafkaRule embeddedKafkaRule() {
        return new EmbeddedKafkaRule(1, true, 1, ITestBase.TOPICS);
    }
}
