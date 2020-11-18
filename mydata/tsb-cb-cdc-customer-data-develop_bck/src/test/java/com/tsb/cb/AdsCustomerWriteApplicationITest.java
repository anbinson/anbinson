//package com.tsb.cb;
//
//import com.tsb.cb.common.ITestBaseWithCassandra;
//import com.tsb.cb.common.TestsConfiguration;
//import com.tsb.ods.model.DeviceRecord;
//import com.tsb.ods.model.HmacRecord;
//import com.tsb.ods.model.ProfileRecord;
//import com.tsb.ods.model.UserRecord;
//import com.tsb.ods.repository.DeviceRepository;
//import com.tsb.ods.repository.HmacRepository;
//import com.tsb.ods.repository.ProfileRepository;
//import com.tsb.ods.repository.TransactionRepository;
//import com.tsb.ods.schema.avro.PE11.PE11Customer;
//import com.tsb.ods.schema.avro.ems.EmsUsersByUserId;
//import com.tsb.ods.schema.avro.tbbvbsenrollments.DeviceByDASUser;
//import com.tsb.ods.stream.schema.CustomerWriteHmacRequest;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.avro.AvroRuntimeException;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.hamcrest.Matchers.equalTo;
//import static org.junit.Assert.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//
//@Slf4j
//@Import(TestsConfiguration.class)
//@ActiveProfiles("test")
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//public class AdsCustomerWriteApplicationITest extends ITestBaseWithCassandra {
//
//    @Autowired
//    ProfileRepository profileRepository;
//    @Autowired
//    TransactionRepository userRepository;
//    @Autowired
//    DeviceRepository deviceRepository;
//    @Autowired
//    HmacRepository hmacRepository;
//
//    @Test
//    public void testEmsUsersByUserIdReceiveAndStore() throws Exception {
//        final EmsUsersByUserId emsUsersByUserId = EmsUsersByUserId.newBuilder()
//                .setDASUSER("das12")
//                .setNUMPERSONA("n12")
//                .setUSERID("u12")
//                .setDASCONTRACT("dc12")
//                .build();
//
//        emsUsersByUserIdTemplate.sendDefault(emsUsersByUserId.getUSERID(), emsUsersByUserId);
//        log.info("sent emsUsersByUserId");
//
//        Thread.sleep(1000);
//
//        List<UserRecord> results = userRepository.findAll();
//        assertThat(results.size(), equalTo(1));
//        if (results.size() > 0) {
//            UserRecord userRecord = results.get(0);
//            assertThat(userRecord.getDasUser(), equalTo("das12"));
//            assertThat(userRecord.getUserId(), equalTo("u12"));
//            assertThat(userRecord.getNumpersona(), equalTo("n12"));
//        }
//    }
//
//    @Test
//    public void testEmsUsersByUserIdReceiveAndUpdate() throws Exception {
//        userRepository.save(UserRecord.builder().userId("u12").hmac("1234").hmacCreatedAt("1234").build());
//
//        final EmsUsersByUserId emsUsersByUserId = EmsUsersByUserId.newBuilder()
//                .setDASUSER("das12")
//                .setNUMPERSONA("n12")
//                .setUSERID("u12")
//                .setDASCONTRACT("dc12")
//                .build();
//
//        emsUsersByUserIdTemplate.sendDefault(emsUsersByUserId.getUSERID(), emsUsersByUserId);
//        log.info("sent emsUsersByUserId");
//
//        Thread.sleep(1000);
//
//        List<UserRecord> results = userRepository.findAll();
//        assertThat(results.size(), equalTo(1));
//        if (results.size() > 0) {
//            UserRecord userRecord = results.get(0);
//            assertThat(userRecord.getDasUser(), equalTo("das12"));
//            assertThat(userRecord.getUserId(), equalTo("u12"));
//            assertThat(userRecord.getNumpersona(), equalTo("n12"));
//            assertThat(userRecord.getHmac(), equalTo("1234"));
//            assertThat(userRecord.getHmacCreatedAt(), equalTo("1234"));
//        }
//    }
//
//    @Test
//    public void testEmsUsersByUserIdNullReceive() {
//        assertThrows(AvroRuntimeException.class, () -> {
//            final EmsUsersByUserId emsUsersByUserId = EmsUsersByUserId.newBuilder()
//                    .setDASUSER("das12")
//                    .setNUMPERSONA(null)
//                    .setUSERID("u12")
//                    .setDASCONTRACT("dc12")
//                    .build();
//
//            emsUsersByUserIdTemplate.sendDefault(emsUsersByUserId.getUSERID(), emsUsersByUserId).get();
//            log.info("sent emsUsersByUserId");
//        });
//    }
//
//    @Test
//    public void testEmsUsersByUserIdEmptyReceive() throws Exception {
//        final EmsUsersByUserId emsUsersByUserId = EmsUsersByUserId.newBuilder()
//                .setDASUSER("das12")
//                .setNUMPERSONA("")
//                .setUSERID("u13")
//                .setDASCONTRACT("dc12")
//                .build();
//
//        emsUsersByUserIdTemplate.sendDefault(emsUsersByUserId.getUSERID(), emsUsersByUserId).get();
//        log.info("sent emsUsersByUserId");
//
//        Thread.sleep(1000);
//
//        List<UserRecord> results = userRepository.findAll();
//        assertThat(results.size(), equalTo(0));
//    }
//
//    @Test
//    public void testDeviceByDASUserReceiveAndStore() throws Exception {
//        final DeviceByDASUser deviceByDASUser = DeviceByDASUser.newBuilder()
//                .setDASUSER("das12")
//                .setDEVICEID("d12")
//                .setDevices(new ArrayList<>())
//                .build();
//
//        deviceByDASUserTemplate.sendDefault(deviceByDASUser.getDASUSER(), deviceByDASUser).get();
//        log.info("sent deviceByDASUser");
//
//        Thread.sleep(1000);
//
//        List<DeviceRecord> results = deviceRepository.findAll();
//        assertThat(results.size(), equalTo(1));
//        if (results.size() > 0) {
//            DeviceRecord deviceRecord = results.get(0);
//            assertThat(deviceRecord.getDasUser(), equalTo("das12"));
//            assertThat(deviceRecord.getDeviceId(), equalTo("d12"));
//        }
//    }
//
//    @Test
//    public void testUserHmacReceiveAndStore() throws Exception {
//        userRepository.save(UserRecord.builder().userId("u12").dasUser("das12").numpersona("n12").build());
//        final CustomerWriteHmacRequest customerWriteHmacRequest = CustomerWriteHmacRequest.newBuilder()
//                .setUserId("u12")
//                .setDeviceId("d12")
//                .setHmac("4bcb287e284f8c21e87e14ba2dc40b16")
//                .setHmacCreatedAt("2018-12-25 09:27:53")
//                .build();
//
//        customerHmacTemplate.sendDefault(customerWriteHmacRequest.getUserId(), customerWriteHmacRequest).get();
//        log.info("sent customerWriteHmacRequest");
//
//        Thread.sleep(1000);
//
//        List<UserRecord> results = userRepository.findAll();
//        assertThat(results.size(), equalTo(1));
//        if (results.size() > 0) {
//            UserRecord userRecord = results.get(0);
//            assertThat(userRecord.getUserId(), equalTo("u12"));
//            assertThat(userRecord.getHmac(), equalTo("4bcb287e284f8c21e87e14ba2dc40b16"));
//            assertThat(userRecord.getHmacDeviceId(), equalTo("d12"));
//            assertThat(userRecord.getHmacCreatedAt(), equalTo("2018-12-25 09:27:53"));
//        }
//
//        List<HmacRecord> results2 = hmacRepository.findAll();
//        assertThat(results2.size(), equalTo(1));
//        if (results2.size() > 0) {
//            HmacRecord hmacRecord = results2.get(0);
//            assertThat(hmacRecord.getUserId(), equalTo("u12"));
//            assertThat(hmacRecord.getDeviceId(), equalTo("d12"));
//            assertThat(hmacRecord.getHmac(), equalTo("4bcb287e284f8c21e87e14ba2dc40b16"));
//            assertThat(hmacRecord.getHmacCreatedAt(), equalTo("2018-12-25 09:27:53"));
//        }
//    }
//
//    @Test
//    public void testUserHmacReceiveAndUpdate() throws Exception {
//        userRepository.save(UserRecord.builder().userId("u12").dasUser("das12").numpersona("n12")
//                .hmac("12345").hmacDeviceId("d15").build());
//        final CustomerWriteHmacRequest customerWriteHmacRequest = CustomerWriteHmacRequest.newBuilder()
//                .setUserId("u12")
//                .setDeviceId("d12")
//                .setHmac("4bcb287e284f8c21e87e14ba2dc40b16")
//                .setHmacCreatedAt("2018-12-25 09:27:53")
//                .build();
//
//        customerHmacTemplate.sendDefault(customerWriteHmacRequest.getUserId(), customerWriteHmacRequest).get();
//        log.info("sent customerWriteHmacRequest");
//
//        Thread.sleep(1000);
//
//        List<UserRecord> results = userRepository.findAll();
//        assertThat(results.size(), equalTo(1));
//        if (results.size() > 0) {
//            UserRecord userRecord = results.get(0);
//            assertThat(userRecord.getUserId(), equalTo("u12"));
//            assertThat(userRecord.getDasUser(), equalTo("das12"));
//            assertThat(userRecord.getNumpersona(), equalTo("n12"));
//            assertThat(userRecord.getHmacDeviceId(), equalTo("d12"));
//            assertThat(userRecord.getHmac(), equalTo("4bcb287e284f8c21e87e14ba2dc40b16"));
//            assertThat(userRecord.getHmacCreatedAt(), equalTo("2018-12-25 09:27:53"));
//        }
//
//        List<HmacRecord> results2 = hmacRepository.findAll();
//        assertThat(results2.size(), equalTo(1));
//        if (results2.size() > 0) {
//            HmacRecord hmacRecord = results2.get(0);
//            assertThat(hmacRecord.getUserId(), equalTo("u12"));
//            assertThat(hmacRecord.getDeviceId(), equalTo("d12"));
//            assertThat(hmacRecord.getHmac(), equalTo("4bcb287e284f8c21e87e14ba2dc40b16"));
//            assertThat(hmacRecord.getHmacCreatedAt(), equalTo("2018-12-25 09:27:53"));
//        }
//    }
//
//    @Test
//    public void testPe11CustomerReceiveAndStore() throws Exception {
//        final PE11Customer pe11Customer = PE11Customer.newBuilder()
//                .setNUMPERSONA(12)
//                .setDESNOMBRE("John")
//                .setDESPRIAPEL("Wick")
//                .setVULNERABLE(1)
//                .build();
//
//        pe11CustomerTemplate.sendDefault(String.valueOf(pe11Customer.getNUMPERSONA()), pe11Customer).get();
//        log.info("sent pe11Customer");
//
//        Thread.sleep(1000);
//
//        List<ProfileRecord> results = profileRepository.findAll();
//        assertThat(results.size(), equalTo(1));
//        if (results.size() > 0) {
//            ProfileRecord ProfileRecord = results.get(0);
//            assertThat(ProfileRecord.getFirstName(), equalTo("John"));
//            assertThat(ProfileRecord.getLastName(), equalTo("Wick"));
//            assertThat(ProfileRecord.getNumpersona(), equalTo("12"));
//        }
//    }
//
//    @Test
//    public void testAllReceiveAndStore() throws Exception {
//        final EmsUsersByUserId emsUsersByUserId = EmsUsersByUserId.newBuilder()
//                .setDASUSER("das12")
//                .setNUMPERSONA("12")
//                .setUSERID("u12")
//                .setDASCONTRACT("dc12")
//                .build();
//
//        final DeviceByDASUser deviceByDASUser = DeviceByDASUser.newBuilder()
//                .setDASUSER("das12")
//                .setDEVICEID("d12")
//                .setDevices(new ArrayList<>())
//                .build();
//
//        final CustomerWriteHmacRequest customerWriteHmacRequest = CustomerWriteHmacRequest.newBuilder()
//                .setUserId("u12")
//                .setHmac("4bcb287e284f8c21e87e14ba2dc40b16")
//                .setHmacCreatedAt("2018-12-25 09:27:53")
//                .setDeviceId("d12")
//                .build();
//
//        final PE11Customer pe11Customer = PE11Customer.newBuilder()
//                .setNUMPERSONA(12)
//                .setDESNOMBRE("John")
//                .setDESPRIAPEL("Wick")
//                .setVULNERABLE(1)
//                .build();
//
//        emsUsersByUserIdTemplate.sendDefault(emsUsersByUserId.getUSERID(), emsUsersByUserId).get();
//        log.info("sent emsUsersByUserId");
//
//        deviceByDASUserTemplate.sendDefault(deviceByDASUser.getDASUSER(), deviceByDASUser).get();
//        log.info("sent deviceByDASUser");
//
//        customerHmacTemplate.sendDefault(customerWriteHmacRequest.getUserId(), customerWriteHmacRequest).get();
//        log.info("sent customerWriteHmacRequest");
//
//        pe11CustomerTemplate.sendDefault(String.valueOf(pe11Customer.getNUMPERSONA()), pe11Customer).get();
//        log.info("sent pe11Customer");
//
//        Thread.sleep(1000);
//
//        List<UserRecord> results1 = userRepository.findAll();
//        assertThat(results1.size(), equalTo(1));
//        if (results1.size() > 0) {
//            UserRecord userRecord = results1.get(0);
//            assertThat(userRecord.getDasUser(), equalTo("das12"));
//            assertThat(userRecord.getUserId(), equalTo("u12"));
//            assertThat(userRecord.getNumpersona(), equalTo("12"));
//            assertThat(userRecord.getHmacDeviceId(), equalTo("d12"));
//            assertThat(userRecord.getHmac(), equalTo("4bcb287e284f8c21e87e14ba2dc40b16"));
//            assertThat(userRecord.getHmacCreatedAt(), equalTo("2018-12-25 09:27:53"));
//        }
//
//        List<ProfileRecord> results2 = profileRepository.findAll();
//        assertThat(results2.size(), equalTo(1));
//        if (results2.size() > 0) {
//            ProfileRecord profileRecord = results2.get(0);
//            assertThat(profileRecord.getFirstName(), equalTo("John"));
//            assertThat(profileRecord.getLastName(), equalTo("Wick"));
//            assertThat(profileRecord.getNumpersona(), equalTo("12"));
//        }
//
//        List<DeviceRecord> results3 = deviceRepository.findAll();
//        assertThat(results2.size(), equalTo(1));
//        if (results2.size() > 0) {
//            DeviceRecord deviceRecord = results3.get(0);
//            assertThat(deviceRecord.getDasUser(), equalTo("das12"));
//            assertThat(deviceRecord.getDeviceId(), equalTo("d12"));
//        }
//
//        List<HmacRecord> results4 = hmacRepository.findAll();
//        assertThat(results4.size(), equalTo(1));
//        if (results4.size() > 0) {
//            HmacRecord hmacRecord = results4.get(0);
//            assertThat(hmacRecord.getUserId(), equalTo("u12"));
//            assertThat(hmacRecord.getDeviceId(), equalTo("d12"));
//            assertThat(hmacRecord.getHmac(), equalTo("4bcb287e284f8c21e87e14ba2dc40b16"));
//            assertThat(hmacRecord.getHmacCreatedAt(), equalTo("2018-12-25 09:27:53"));
//        }
//    }
//}