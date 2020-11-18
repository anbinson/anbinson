# Conversational Banking - CDC Customer Data Write Topology
## Description
CDC Customer Data Topology reads CDC public / private topics and creates a custom Customer table (numpersona, userid, deviceid, etc.) in ScyllaDB as well as a kstream containing the customer data.

## Technology
* Java 11
* Spring Boot
* ScyllaDB
* Kafka

## Build
`mvn clean install`

## Monitoring
Ensure Prometheus has access to /actuator/prometheus.

## Database
Run the CQL queries for each environment (under /resources/cql).
### DEV
```
CREATE KEYSPACE IF NOT EXISTS tsbcb WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3};
CREATE TABLE IF NOT EXISTS tsbcb.user(user_id text, das_user text, numpersona text, hmac text, hmac_created_at text, hmac_device_id text, PRIMARY KEY (user_id));
CREATE TABLE IF NOT EXISTS tsbcb.device(das_user text, device_id text, PRIMARY KEY (das_user));
CREATE TABLE IF NOT EXISTS tsbcb.profile(numpersona text, first_name text, last_name text,  PRIMARY KEY (numpersona));
CREATE TABLE IF NOT EXISTS tsbcb.hmac(hmac text, user_id text, device_id text, hmac_created_at text, PRIMARY KEY (hmac));
```
### SIT
```
CREATE KEYSPACE IF NOT EXISTS tsbcb WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3};
CREATE TABLE IF NOT EXISTS tsbcb.user(user_id text, das_user text, numpersona text, hmac text, hmac_created_at text, hmac_device_id text, PRIMARY KEY (user_id));
CREATE TABLE IF NOT EXISTS tsbcb.device(das_user text, device_id text, PRIMARY KEY (das_user));
CREATE TABLE IF NOT EXISTS tsbcb.profile(numpersona text, first_name text, last_name text,  PRIMARY KEY (numpersona));
CREATE TABLE IF NOT EXISTS tsbcb.hmac(hmac text, user_id text, device_id text, hmac_created_at text, PRIMARY KEY (hmac));
```
## Topics
### DEV
* com.tsb.private.ems.users_by_user_id
* com.tsb.private.cdc.tbbv_bs_enrollments
* com.tsb.cb.customer.write.hmac.public.request
* com.tsb.private.cdc.pe11.customers

### SIT
* com.tsb.private.cb.users_by_user_id.0
* com.tsb.private.cb.tbbv_bs_enrollments.0
* com.tsb.cb.customer.write.hmac.public.request.0
* com.tsb.private.cb.pe11.customers.0
