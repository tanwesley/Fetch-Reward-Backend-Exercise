# Fetch-Reward-Backend-Exercise
A simple Spring Boot application that keeps track of a user's points balance, with routes to add transactions and spend points from the available balance.

## Requirements

## How to use

### Installation

### Running the Spring application 
1. Open the command line terminal
2. Navigate to the root directory of the project.
3. Execute the following line:


### How to make requests
#### Add a transaction 
curl -H "Content-Type: application/json" -d "{ \"payer\": \"DANNON\", \"points\": 1000, \"timestamp\": \"2020-11-02T14:00:00Z\" }" http://localhost:8080/addTransaction

#### Spend points
curl -H "Content-Type: application/json" -d "{ \"points\": 1000 }" http://localhost:8080/spendPoints
