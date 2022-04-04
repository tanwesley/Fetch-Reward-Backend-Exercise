package com.tanwesley.fetchrewards.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tanwesley.fetchrewards.model.SpendRequest;
import com.tanwesley.fetchrewards.model.TransactionReport;
import exception.NegativePointException;
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
    // <Payer, Points>
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

        // Map payer with updated point balance, default value set to 0 if key does not yet exist in the HashMap.
        updateTransactionHashMap(transaction);
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

        while (pointsToSpend != 0) {
            TransactionReport oldestTransaction = transactionReports.poll();

            if (oldestTransaction == null) throw new NegativePointException("ERROR: Not enough points in balance");

            int pointsToUseFromOldest;

            if (oldestTransaction.getPoints() <= pointsToSpend) {
                pointsToUseFromOldest = oldestTransaction.getPoints();
            } else {
                pointsToUseFromOldest = pointsToSpend;

                // update transaction report if not all funds are used from a transaction
                transactionReports.add(new TransactionReport(oldestTransaction.getPayer(),
                        oldestTransaction.getPoints() - pointsToUseFromOldest, oldestTransaction.getTimestamp()));
            }

            pointsToSpend -= pointsToUseFromOldest;

            spendReportMap.put(oldestTransaction.getPayer(),
                    spendReportMap.getOrDefault(oldestTransaction.getPayer(), 0) - pointsToUseFromOldest);
        }

        List<TransactionReport> spendReportsList = new ArrayList<>();

        // Populate array list of spend reports to return as a JSON list using data from the spendReportMap hashmap.
        // Spend reports are marked with the current timestamp.
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

    private void updateTransactionHashMap(TransactionReport transaction) {

        payerPointBalances.put(transaction.getPayer(),
                payerPointBalances.getOrDefault(transaction.getPayer(), 0) + transaction.getPoints());

        // remove any payers with 0 balances
        payerPointBalances.values().removeIf(f -> f == 0f);
    }


}
