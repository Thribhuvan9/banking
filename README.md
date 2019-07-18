# Revolut Home-Taks: Banking
RESTful API to transfers money between accounts.

## Table of Contents
- [Installation](#installation)
- [Tests](#Tests)
- [APIs](#APIs)
- [Tech Stack](#TechStack)
- [Author](#Author)



##Installation
### Clone

- Clone this repo to your local machine using url `https://github.com/Thribhuvan9/banking.git`
```shell
 git clone https://github.com/Thribhuvan9/banking.git
```
### Setup

- Below are the steps to build and run the applications:

> To build applications
```
 ./gradlew clean build
```

> To run applications
```
 ./gradlew run
```
- Note: For windows OS use `gradlew.bat` instead of `./gradlew` to run 

- Note: Server will run on port `4567`. [Server Link](http://localhost:4567/healthcheck)

- Once server is started will get below output in console
```
 [Thread-0] INFO org.eclipse.jetty.server.ServerConnector - Started ServerConnector@93da644{HTTP/1.1,[http/1.1]}{0.0.0.0:4567}
 [Thread-0] INFO org.eclipse.jetty.server.Server - Started @322ms
```
- Note: Server will run on port `4567` [Server Link](http://localhost:4567/healthcheck)
### Create jar for deployment
- To build jar, execute:

```
 ./gradlew shadowJar
```

- To run standalone server, execute:

```
 java -jar build/libs/banking-1.0-SNAPSHOT.jar
```

- Note: Server will run on port `4567` [Server Link](http://localhost:4567/healthcheck)

- Once server is started will get below output in console
```
  INFO com.thl.banking.service.AccountService - Request received to get ALL user accounts
  <<<=========----> 75% EXECUTING 
  > :run
```
- To stop the server press key, `CONTROL + c` or `^ + c`

##Tests
- To run all JUnit Test, execute:
```
 ./gradlew test
```

- To execute only **API tests** written with REST Assured, execute:

```
 ./gradlew test -Dtest.profile=integration
```

- To generate test coverage report, run the following command:

```
./gradlew test jacocoTestReport
```
- All report can be found in `build/reports/jacoco/` folder

##APIs
- Attached postman json file in location `Postman_collection/BankApp.postman_collection.json` for all the API.
    ####APIs List
    - [Health check](#Health check)
    - [Accounts](#Accounts)
    - [Transactions](#Transactions)
    
    
###Health check

```
GET /healthcheck
```

- curl request:

```
curl -X GET \
  http://localhost:4567/healthcheck 
```

###Accounts

#### Creating account

```
POST /account
```

- form params: `id`, `name`, `Address` , `balance` and `currencyUnit`

- curl request:

```
curl -X POST \
  http://localhost:4567/account \
  -H 'Content-Type: application/json' \
  -d '{
    "user":{
        "id" :"12435",
        "name" :"John",
        "Address": "Bangalore"
      },
  "balance":"10000",
  "currencyUnit":"INR"
}'
```
- Response: `201`:`CREATED` 
```{
       "accountId": "443e7fca-4747-42c2-933a-8e63636bceab",
       "user": {
           "id": "12435",
           "name": "John"
       },
       "balance": 10000,
       "createdTime": "2019-07-18T19:36:31.911",
       "updatedTime": "2019-07-18T19:36:31.911",
       "currencyUnit": "INR"
   }
```
#### Deleting account

```
DELETE /account/{id}
```

- path param: `id`

- curl request:

```
curl -X DELETE \
  http://localhost:4567/account/f1ba2431-8aae-495b-bffe-0c76ea4357e7
```

- Response: `202`:`ACCEPTED` 
```
"{ "status": 202,  "message": "success"}"
```
#### Getting one account

```
GET /account/{id}
```

- path param: `id`

- curl request:

```
curl -X GET \
  http://localhost:4567/account/03732e1a-0c5b-4818-86f7-e6adca4d0ed8
```
- Response: `200`:`OK` 
``` 
{
    "accountId": "443e7fca-4747-42c2-933a-8e63636bceab",
    "user": {
        "id": "12435",
        "name": "John"
    },
    "balance": 10000,
    "createdTime": "2019-07-18T19:36:31.911",
    "updatedTime": "2019-07-18T19:36:31.911",
    "currencyUnit": "INR"
}
```
#### Getting all accounts

```
GET /account
```

- curl request:

```
curl -X GET \
  http://localhost:4567/account
```
- Response: `200`:`OK`

``` 
{
    "443e7fca-4747-42c2-933a-8e63636bceab": {
        "accountId": "443e7fca-4747-42c2-933a-8e63636bceab",
        "user": {
            "id": "12435",
            "name": "John"
        },
        "balance": 10000,
        "createdTime": "2019-07-18T19:36:31.911",
        "updatedTime": "2019-07-18T19:36:31.911",
        "currencyUnit": "INR"
    },
    "4fe40d39-5d7a-4532-95c9-e2664f1cfd4c": {
        "accountId": "4fe40d39-5d7a-4532-95c9-e2664f1cfd4c",
        "user": {
            "id": "12435",
            "name": "Krishna"
        },
        "balance": 90000,
        "createdTime": "2019-07-18T19:36:24.815",
        "updatedTime": "2019-07-18T19:36:24.815",
        "currencyUnit": "INR"
    },
    ...
}
```

###Transactions

#### Committing transaction

```
POST /transaction
```

- form params: `sender`, `receiver`, `amount` and `currency`

- curl request:

```
curl -X POST \
  http://localhost:4567/transaction \
  -H 'Content-Type: application/json' \
  -d '{
  "sender":"443e7fca-4747-42c2-933a-8e63636bceab",
  "receiver": "4fe40d39-5d7a-4532-95c9-e2664f1cfd4c",
  "amount": "1000",
  "currency": "INR"
}'
```
- Response: `200`:`OK`

``` 
{
    "id": "276d29eb-014b-43d3-b921-0c1468d75df7",
    "from": {
        "accountId": "443e7fca-4747-42c2-933a-8e63636bceab",
        "user": {
            "id": "12435",
            "name": "John"
        },
        "balance": 6000,
        "createdTime": "2019-07-18T19:36:31.911",
        "updatedTime": "2019-07-18T19:36:31.911",
        "currencyUnit": "INR"
    },
    "to": {
        "accountId": "4fe40d39-5d7a-4532-95c9-e2664f1cfd4c",
        "user": {
            "id": "12435",
            "name": "Krishna"
        },
        "balance": 94000,
        "createdTime": "2019-07-18T19:36:24.815",
        "updatedTime": "2019-07-18T19:36:24.815",
        "currencyUnit": "INR"
    },
    "amount": 1000,
    "createdTime": "2019-07-18T19:37:52.555",
    "currencyUnit": "INR",
    "status": "TRANSACTION SUCCESSFUL"
}
```

#### Getting one transaction

```
GET /transaction/{id}
```

- path param: `id`

- curl request:

```
curl -X GET \
  http://localhost:4567/transaction/276d29eb-014b-43d3-b921-0c1468d75df7
```
- Response: `200`:`OK`

``` 
{
    "id": "276d29eb-014b-43d3-b921-0c1468d75df7",
    "from": {
        "accountId": "443e7fca-4747-42c2-933a-8e63636bceab",
        "user": {
            "id": "12435",
            "name": "John"
        },
        "balance": 6000,
        "createdTime": "2019-07-18T19:36:31.911",
        "updatedTime": "2019-07-18T19:36:31.911",
        "currencyUnit": "INR"
    },
    "to": {
        "accountId": "4fe40d39-5d7a-4532-95c9-e2664f1cfd4c",
        "user": {
            "id": "12435",
            "name": "Krishna"
        },
        "balance": 94000,
        "createdTime": "2019-07-18T19:36:24.815",
        "updatedTime": "2019-07-18T19:36:24.815",
        "currencyUnit": "INR"
    },
    "amount": 1000,
    "createdTime": "2019-07-18T19:37:52.555",
    "currencyUnit": "INR",
    "status": "TRANSACTION SUCCESSFUL"
}
```

#### Getting all transactions

```
GET /transaction
```

- curl request:

```
curl -X GET \
  http://localhost:4567/transaction 
```
- Response: `200`:`OK`

``` 
{
    {
        "id": "276d29eb-014b-43d3-b921-0c1468d75df7",
        "from": {
            "accountId": "443e7fca-4747-42c2-933a-8e63636bceab",
            ...
        },
        "to": {
            "accountId": "4fe40d39-5d7a-4532-95c9-e2664f1cfd4c",
            ...
        },
        "amount": 1000,
        "createdTime": "2019-07-18T19:37:52.555",
        "currencyUnit": "INR",
        "status": "TRANSACTION SUCCESSFUL"
        },
        ...
}
```

##TechStack

- Below are the technology stacks used.

- **Application**: Java 8, Gradle, Google's Dagger, Spark Framework , Slf4J, Google GSON
- **Tests**: JUnit, Mockito, Truth, Concurrent Unit, REST Assured

##Author
* **Thribhuvan H L**  [Git](https://github.com/Thribhuvan9) [LinkedIn](https://www.linkedin.com/in/thribhuvan-lokesh/)

