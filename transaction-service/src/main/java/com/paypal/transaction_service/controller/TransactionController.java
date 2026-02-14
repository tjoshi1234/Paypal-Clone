package com.paypal.transaction_service.controller;

import com.paypal.transaction_service.dto.TransferRequest;
import com.paypal.transaction_service.entity.Transaction;
import com.paypal.transaction_service.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody Transaction transaction) {
        Transaction created = service.createTransaction(transaction);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok().body(service.getAllTransactions());
    }
}
