package com.bankingsystem.card.helper;

import java.time.LocalDate;
import java.time.Year;
import java.util.Random;

import com.bankingsystem.card.entity.Card;
import com.bankingsystem.card.entity.CardNetwork;
import com.bankingsystem.card.entity.CardStatus;
import com.bankingsystem.card.entity.CardType;

public class CardUtils {

    private static final Random random = new Random();


    public static Card generateCard(Long accountId, CardType type, CardNetwork network, String holderName) {
        return Card.builder()
                .cardNumber(generateCardNumber(network))
                .cardHolderName(holderName)
                .expiryDate(generateExpiryDate())
                .cvv(generateCVV(network))
                .cardNetwork(network)
                .cardType(type)
                .status(CardStatus.ACTIVE)
                .accountId(accountId)
                .build();
    }

    public static String generateCardNumber(CardNetwork network) {
        String prefix = getNetworkPrefix(network);
        int length = getCardNumberLength(network);
        
        StringBuilder cardNumber = new StringBuilder(prefix);
        int remainingLength = length - prefix.length() - 1; // -1 for check digit

        for (int i = 0; i < remainingLength; i++) {
            cardNumber.append(random.nextInt(10));
        }

        String partialNumber = cardNumber.toString();
        int checkDigit = calculateLuhnCheckDigit(partialNumber);
        cardNumber.append(checkDigit);

        return cardNumber.toString();
    }

    private static String getNetworkPrefix(CardNetwork network) {
        switch (network) {
            case VISA: return "4";
            case MASTERCARD: return "5";
            case AMEX: return "37";
            case DISCOVER: return "6";
            case MEEZA: return "3"; 
            default: return "4"; 
        }
    }

    private static int getCardNumberLength(CardNetwork network) {
        return network == CardNetwork.AMEX ? 15 : 16;
    }

    private static int calculateLuhnCheckDigit(String number) {
        int sum = 0;
        boolean alternate = false;

        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }

        return (10 - (sum % 10)) % 10;
    }

    public static String generateCVV(CardNetwork network) {
        int length = network == CardNetwork.AMEX ? 4 : 3;
        return generateCVV(length);
    }

    public static String generateCVV(int length) {
        if (length < 3 || length > 4) {
            throw new IllegalArgumentException("CVV length must be 3 or 4 digits");
        }

        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;
        return String.valueOf(random.nextInt(max - min + 1) + min);
    }

    public static LocalDate generateExpiryDate() {
        int currentYear = Year.now().getValue();
        int year = currentYear + random.nextInt(5);
        int month = random.nextInt(12) + 1;
        
        // Ensure the date is at least 1 month in the future
        if (year == currentYear) {
            int currentMonth = LocalDate.now().getMonthValue();
            month = Math.max(month, currentMonth + 1);
        }
        
        return LocalDate.of(year, month, 1).withDayOfMonth(
            LocalDate.of(year, month, 1).lengthOfMonth()
        );
    }

    


    public static boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || !cardNumber.matches("\\d+")) {
            return false;
        }

        CardNetwork network = detectNetwork(cardNumber);
        int expectedLength = getCardNumberLength(network);
        
        if (cardNumber.length() != expectedLength) {
            return false;
        }

        // Luhn check
        int sum = 0;
        boolean alternate = false;
        
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }

        return (sum % 10) == 0;
    }

    public static CardNetwork detectNetwork(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return null;
        }

        if (cardNumber.startsWith("4")) return CardNetwork.VISA;
        if (cardNumber.startsWith("5")) return CardNetwork.MASTERCARD;
        if (cardNumber.startsWith("37")) return CardNetwork.AMEX;
        if (cardNumber.startsWith("6")) return CardNetwork.DISCOVER;
        if (cardNumber.startsWith("3")) return CardNetwork.MEEZA;
        
        return null;
    }
}