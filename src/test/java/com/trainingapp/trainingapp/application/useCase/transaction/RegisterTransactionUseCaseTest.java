package com.trainingapp.trainingapp.application.useCase.transaction;

import com.trainingapp.trainingapp.domain.entity.transaction.Transaction;
import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;
import com.trainingapp.trainingapp.domain.enums.transaction.TransactionCategory;
import com.trainingapp.trainingapp.domain.repository.transaction.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterTransactionUseCaseTest {

    @Mock private TransactionRepository transactionRepository;

    @InjectMocks private RegisterTransactionUseCase useCase;

    @Test
    @DisplayName("Debería registrar y guardar una transacción exitosamente")
    void shouldRegisterTransactionSuccessfully() {
        RegisterTransactionCommand command = new RegisterTransactionCommand(
                new BigDecimal("5000.00"), PaymentMethod.CASH, TransactionCategory.MEMBERSHIP,
                "Pago mensualidad", 10L, 1L, 100L, null
        );

        Transaction mockSavedTransaction = mock(Transaction.class);
        when(mockSavedTransaction.getAmount()).thenReturn(new BigDecimal("5000.00"));

        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockSavedTransaction);

        Transaction result = useCase.execute(command);

        assertNotNull(result);
        assertEquals(new BigDecimal("5000.00"), result.getAmount());

        // Verificamos que se haya intentado guardar en el repositorio
        verify(transactionRepository).save(any(Transaction.class));
    }
}