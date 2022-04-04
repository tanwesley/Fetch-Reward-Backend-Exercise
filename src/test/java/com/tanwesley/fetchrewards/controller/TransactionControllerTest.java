package com.tanwesley.fetchrewards.controller;

import com.tanwesley.fetchrewards.model.SpendRequest;
import com.tanwesley.fetchrewards.model.TransactionReport;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class TransactionControllerTest {
    TransactionController controller = new TransactionController();

    TransactionReport testTransaction1
            = new TransactionReport("TEST PAYER 1", 1000, LocalDateTime.of(
            2022, Month.JANUARY, 1,
            0, 1));

    TransactionReport testTransaction2
            = new TransactionReport("TEST PAYER 2", 2000, LocalDateTime.of(
            2022, Month.JANUARY, 1,
            0, 0));

    TransactionReport testTransaction3
            = new TransactionReport("TEST PAYER 3", -1000, LocalDateTime.of(
            2022, Month.FEBRUARY, 1,
            0, 0));

    String testPayerPointsResponse = "{\n\"TEST PAYER 1\": 1000, " +
            "\n\"TEST PAYER 2\": 2000 \n}";

    @Test
    void simpleAddTransactionTest() {
        controller.addTransaction(testTransaction1);

        assertEquals(1000, controller.getTotalPoints());
    }

    @Test
    void addAndSpendFromOneTransaction() {
        controller.addTransaction(testTransaction2);
        controller.spendPoints(new SpendRequest(100));

        // test for total points updating correctly
        assertEquals(1900, controller.getTotalPoints());
    }

    @Test
    void addNegativeTransaction() {
        controller.addTransaction(testTransaction2);
        controller.addTransaction(testTransaction3);

        assertEquals(1000, controller.getTotalPoints());
    }

    @Test
    void timeStampSortingTest() {
        controller.addTransaction(testTransaction1);    // january 1, 2022 12:01AM
        controller.addTransaction(testTransaction2);    // january 1, 2022 12:00AM
        controller.addTransaction(testTransaction3);    // february 1, 2022 12:00AM

        assertEquals(testTransaction2, controller.getTransactionReports().poll());
        assertEquals(testTransaction1, controller.getTransactionReports().poll());
        assertEquals(testTransaction3, controller.getTransactionReports().poll());

    }
}