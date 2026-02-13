package com.paypal.transaction_service.service;

import com.paypal.transaction_service.entity.Transaction;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

public interface TransactionService {

    Transaction createTransaction(Transaction transaction);
    List<Transaction> getAllTransactions();
}
