package com.project.bankacquirer.model;

public enum TransactionStatus {
    INITIATED, RESERVED_FUNDS, SUCCESSFUL, FAILED, ERROR;

    public static TransactionStatus fromString(String transactionStatus) {
        return switch (transactionStatus) {
            case "INITIATED" -> INITIATED;
            case "RESERVED_FUNDS" -> RESERVED_FUNDS;
            case "SUCCESSFUL" -> SUCCESSFUL;
            case "FAILED" -> FAILED;
            case "ERROR" -> ERROR;
            default -> throw new IllegalArgumentException("Invalid transaction status: " + transactionStatus);
        };
    }
}
