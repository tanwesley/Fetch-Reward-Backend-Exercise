# Fetch-Reward-Backend-Exercise
A simple Spring Boot application that keeps track of a user's points balance, with routes to add transactions and spend points from the available balance.

## Framework Used:
Spring Boot 2.6.6

## Requirements
1. Java 17
2. Maven 3.8.5

## How to use


### Installation

### Running the Spring application 
1. Open the command line terminal
2. Navigate to the root directory of the project.
3. Execute the following line in the command line to start the application on the local host:
```
mvn spring-boot:run
```

### How to make requests

Now open a new command terminal to execute commands that send requests to the application.
 

#### Add a transaction (/addTransaction)

```
curl -H "Content-Type: application/json" -d "{ \"payer\": \"DANNON\", \"points\": 1000, \"timestamp\": \"2020-11-02T14:00:00Z\" }" http://localhost:8080/addTransaction
```

#### Spend points (/spendPoints)
```
curl -H "Content-Type: application/json" -d "{ \"points\": 1000 }" http://localhost:8080/spendPoints
```


#### Get point balances per payer (/payerPointBalances)
```
curl http://localhost:8080/payerPointBalances
```
