# Fetch-Reward-Backend-Exercise
A simple Spring Boot application that keeps track of a user's points balance, with routes to add transactions and spend points from the available balance.

## Framework Used:
Spring Boot 2.6.6

## Requirements
1. Java 17
 ** Download link: https://www.oracle.com/java/technologies/downloads/
3. Maven 3.8.5
 ** Download link: https://maven.apache.org/download.cgi

## How to use


### Installation
1. Open command line terminal.
2. Navigate to a directory to copy the project into.
3. Execute the following line in the command terminal.
 
 ```git clone https://github.com/tanwesley/Fetch-Reward-Backend-Exercise```


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
** This command sends a POST request to the /addTransaction route and adds a new transaction with payer, points, and timestamp fields.

```
curl -X POST -H "Content-Type: application/json" -d "{ \"payer\": \"DANNON\", \"points\": 1000, \"timestamp\": \"2020-11-02T14:00:00Z\" }" http://localhost:8080/addTransaction
```

#### Spend points (/spendPoints)
** This command sends a PUT request to /spendPoints and spends points from payers given the user has enough points in their balance. The transactions with the oldest timestamps are the first to have points drawn from.

```
curl -X PUT -H "Content-Type: application/json" -d "{ \"points\": 100 }" http://localhost:8080/spendPoints
```


#### Get point balances per payer (/payerPointBalances)
** This command sends a GET request to /payerPointBalances and returns a JSON response with the user's point balances per payer.

```
curl http://localhost:8080/payerPointBalances
```
