package com.tanwesley.fetchrewards.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tanwesley.fetchrewards.model.SpendRequest;
import com.tanwesley.fetchrewards.model.TransactionReport;
import com.tanwesley.fetchrewards.exception.NegativePointException;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.*;

import java.time.LocalDateTime;
import java.util.*;
import java.lang.Math;

@RestController
public class TransactionController {

    // ObjectMapper for JSON conversion and formatting
    private ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);


    // Total points from all payers
    private int totalPoints = 0;

    // HashMap to keep track of payer point balances.
    // <key = Payer, value = Points>
    private Map<String, Integer> payerPointBalances = new HashMap<>();


    // A priority queue to keep track of transaction reports
    private Queue<TransactionReport> transactionReports = new PriorityQueue<>(new Comparator<TransactionReport>() {
        // Set priority queue to sort TransactionReport objects by timestamp.
        // Oldest timestamps are first out in priority.
        @Override
        public int compare(TransactionReport t1, TransactionReport t2) {
            return t1.getTimestamp().compareTo(t2.getTimestamp());
        }
    });

    // Adding a transaction report to the priority queue
    @PostMapping("/addTransaction")
    public void addTransaction(@RequestBody TransactionReport transaction) {

        // If an add transaction request with negative points is sent and there are not enough total points in
        // the user's balance, the method will throw an error.
        if ((transaction.getPoints() < 0) && (totalPoints < Math.abs(transaction.getPoints()))) {
            throw new NegativePointException("ERROR: Not enough points in account.");
        }

        transactionReports.add(transaction);

        // Map payer with updated point balance.
        updatePayerPointBalancesMap(transaction);

        totalPoints += transaction.getPoints();
    }

    // Submitting a spend request
    @PutMapping("/spendPoints")
    public List<TransactionReport> spendPoints(@RequestBody SpendRequest request) throws NegativePointException {
        int pointsToSpend = request.getPoints();

        // Throw error if user does not have enough points to spend.
        if (pointsToSpend > totalPoints) throw new NegativePointException("ERROR: Not enough points in balance");

        // A hashmap to keep track of how many points are taken per payer to fulfill the spend request.
        Map<String, Integer> spendReportMap = new HashMap<>();


        // Start a while loop that goes until there are no more points to spend from the spend request.
        while (pointsToSpend != 0) {
            // First, the transaction with the oldest timestamp is polled from the priority queue.
            TransactionReport oldestTransaction = transactionReports.poll();

            // If there are no transactions in the priority queue, the method will throw an error.
            if (oldestTransaction == null) throw new NegativePointException("ERROR: Not enough points in balance");

            // Initialize an int variable to hold the amount of points to subtract from the oldest transaction in the
            // current iteration of the loop.
            int pointsToUseFromOldest;

            if (oldestTransaction.getPoints() <= pointsToSpend) {
                pointsToUseFromOldest = oldestTransaction.getPoints();
            } else {
                // If there are more points in the oldest transaction than points in the spend request, we subtract
                // the points to spend from the oldest transaction's point balance.
                pointsToUseFromOldest = pointsToSpend;
                /*
                Update transaction report if not all funds are used from a transaction.
                Because we have polled the oldest transaction out of the priority queue but not all points have been
                used from this transaction, we need to add that transaction back in with the remaining points.
                The transaction added back will be marked with the original timestamp in order to preserve the order
                within the priority queue.
                */
                transactionReports.add(new TransactionReport(oldestTransaction.getPayer(),
                        oldestTransaction.getPoints() - pointsToUseFromOldest, oldestTransaction.getTimestamp()));
            }

            pointsToSpend -= pointsToUseFromOldest;

            spendReportMap.put(oldestTransaction.getPayer(),
                    spendReportMap.getOrDefault(oldestTransaction.getPayer(), 0) - pointsToUseFromOldest);
        }

        List<TransactionReport> spendReportsList = new ArrayList<>();

        // Populate an array list of spend reports to return as a JSON list using data from the spendReportMap hashmap.
        // Spend reports are marked with a timestamp of the current time that the spend request is executed.
        spendReportMap.forEach(
                (key, value) -> spendReportsList.add(new TransactionReport(key, value, LocalDateTime.now())));

        // Add all spend reports that fulfilled the spend request to the priority queue.
        for (TransactionReport t : spendReportsList) {
            addTransaction(t);
        }

        return spendReportsList;
    }

    // Return point balances per payer in JSON format
    @GetMapping("/payerPointBalances")
    public String getPayerPointBalances() throws JsonProcessingException {
        return objectMapper.writeValueAsString(payerPointBalances);
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public Queue<TransactionReport> getTransactionReports() {
        return transactionReports;
    }

    private void updatePayerPointBalancesMap(TransactionReport transaction) {

        // Updates the hashmap tracking points per payer. The default value is set to 0 if key does not yet exist in the HashMap.
        payerPointBalances.put(transaction.getPayer(),
                payerPointBalances.getOrDefault(transaction.getPayer(), 0) + transaction.getPoints());

        // remove any payers with 0 balances
        payerPointBalances.values().removeIf(f -> f == 0f);
    }

}
