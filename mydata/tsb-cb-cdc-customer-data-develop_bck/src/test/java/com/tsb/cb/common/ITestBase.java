package com.tsb.cb.common;


import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

import com.tsb.ods.schema.avro.PE11.PE11Customer;
import com.tsb.ods.schema.avro.ems.EmsUsersByUserId;
import com.tsb.ods.schema.avro.tbbvbsenrollments.DeviceByDASUser;
import com.tsb.ods.stream.schema.CustomerWriteHmacRequest;

import java.util.Map;

@RunWith(SpringRunner.class)
public class ITestBase {

    public static final String EMS_USER_BY_USER_ID_INPUT = "com.tsb.private.cb.users_by_user_id";
    public static final String DEVICE_BY_DASUSER_INPUT = "com.tsb.private.cb.tbbv_bs_enrollments";
    public static final String CUSTOMER_HMAC_INPUT = "com.tsb.cb.customer.write.hmac.public.request";
    public static final String PE11_CUSTOMERS_INPUT = "com.tsb.private.cb.pe11.customers";
    public static final String[] TOPICS = {EMS_USER_BY_USER_ID_INPUT, DEVICE_BY_DASUSER_INPUT, CUSTOMER_HMAC_INPUT, PE11_CUSTOMERS_INPUT};
    public static final Long TIMEOUT = 10000L;

    protected KafkaTemplate<String, EmsUsersByUserId> emsUsersByUserIdTemplate;
    protected KafkaTemplate<String, DeviceByDASUser> deviceByDASUserTemplate;
    protected KafkaTemplate<String, CustomerWriteHmacRequest> customerHmacTemplate;
    protected KafkaTemplate<String, PE11Customer> pe11CustomerTemplate;

    @Autowired
    protected EmbeddedKafkaBroker embeddedKafkaBroker;
    protected DummySpecificAvroSerde serde = new DummySpecificAvroSerde();


    @Before
    public void setupKafka() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("group", "false", embeddedKafkaBroker);
        consumerProps.put(StreamsConfig.APPLICATION_ID_CONFIG, "CustomerWriteApplicationTestBase");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put("specific.avro.reader", true);
        consumerProps.put("schema.registry.url", "http://fake:8081");


        serde.configure(consumerProps, false);

        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        producerProps.put(StreamsConfig.APPLICATION_ID_CONFIG, "CustomerDataWriteConfigurationTestBase");
        producerProps.put("schema.registry.url", "http://fake:8081");
        producerProps.put("auto.register.schemas", "true");
        producerProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 1);
        producerProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);

        this.emsUsersByUserIdTemplate = new KafkaTemplate<>(createProducerFactor(producerProps), true);
        this.emsUsersByUserIdTemplate.setDefaultTopic(EMS_USER_BY_USER_ID_INPUT);

        this.deviceByDASUserTemplate = new KafkaTemplate<>(createProducerFactor(producerProps), true);
        this.deviceByDASUserTemplate.setDefaultTopic(DEVICE_BY_DASUSER_INPUT);

        this.customerHmacTemplate = new KafkaTemplate<>(createProducerFactor(producerProps), true);
        this.customerHmacTemplate.setDefaultTopic(CUSTOMER_HMAC_INPUT);

        this.pe11CustomerTemplate = new KafkaTemplate<>(createProducerFactor(producerProps), true);
        this.pe11CustomerTemplate.setDefaultTopic(PE11_CUSTOMERS_INPUT);
    }

    private <T> DefaultKafkaConsumerFactory<String, T> createConsumerFactory(Map<String, Object> consumerProps) {
        return new DefaultKafkaConsumerFactory<String, T>(
                consumerProps,
                new StringDeserializer(),
                serde.deserializer()
        );
    }

    private <T> DefaultKafkaProducerFactory<String, T> createProducerFactor(Map<String, Object> producerProps) {
        return new DefaultKafkaProducerFactory<String, T>(
                producerProps,
                new StringSerializer(),
                serde.serializer()
        );
    }

    public static class DummySpecificAvroSerde extends SpecificAvroSerde {

        private static final MockSchemaRegistryClient client = new MockSchemaRegistryClient();

        public DummySpecificAvroSerde() {
            super(client);
        }
    }

}
