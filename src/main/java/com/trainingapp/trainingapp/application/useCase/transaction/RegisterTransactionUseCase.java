package com.trainingapp.trainingapp.application.useCase.transaction;

import com.trainingapp.trainingapp.domain.entity.transaction.Transaction;
import com.trainingapp.trainingapp.domain.repository.transaction.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterTransactionUseCase {

    private final TransactionRepository transactionRepository;

    public RegisterTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction execute(RegisterTransactionCommand command) {
        Transaction transaction = createTransactionFrom(command);

        return transactionRepository.save(transaction);
    }

    private Transaction createTransactionFrom(RegisterTransactionCommand command) {
        return Transaction.createNew(
                command.amount(),
                command.paymentMethod(),
                command.category(),
                command.notes(),
                command.gymId(),
                command.registeredByAdminId(),
                command.subscriptionId(),
                command.saleId()
        );
    }
}