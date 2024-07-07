package theatricalplays;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

record StatementData(String customer, List<Performance> performances) {
}

public class StatementPrinter {
    private Map<String, Play> plays;

    public String print(Invoice invoice, Map<String, Play> plays) {
        this.plays = plays;

        var statementData = new StatementData(
                invoice.customer,
                invoice.performances.stream().map(this::enrichPerformance).toList()
        );
        return renderPlainText(statementData);
    }

    private Performance enrichPerformance(Performance aPerformance) {
        var printer = new StatementPrinter() {
            private Play playFor(Performance aPerformance) {
                return plays.get(aPerformance.playID);
            }
        };
        var result = new Performance(aPerformance.playID, aPerformance.audience);

        result.play = printer.playFor(result);
        return result;
    }

    private String renderPlainText(StatementData data) {
        StringBuilder result = new StringBuilder(String.format("Statement for %s\n", data.customer()));

        for (var aPerformance : data.performances()) {
            // print line for this order
            result.append(String.format("  %s: %s (%s seats)\n", aPerformance.play.name, usd(amountFor(aPerformance) / 100), aPerformance.audience));
        }

        result.append(String.format("Amount owed is %s\n", usd(totalAmount(data) / 100)));
        result.append(String.format("You earned %s credits\n", totalVolumeCredits(data)));
        return result.toString();
    }

    private int totalAmount(StatementData data) {
        var totalAmount = 0;
        for (var aPerformance : data.performances()) {
            totalAmount += amountFor(aPerformance);
        }
        return totalAmount;
    }

    private int totalVolumeCredits(StatementData data) {
        var volumeCredits = 0;
        for (var aPerformance : data.performances()) {
            volumeCredits += volumeCreditsFor(aPerformance);
        }
        return volumeCredits;
    }

    private String usd(int aNumber) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(aNumber);
    }

    private int volumeCreditsFor(Performance aPerformance) {
        var volumeCredits = Math.max(aPerformance.audience - 30, 0);

        if ("comedy".equals(aPerformance.play.type)) {
            volumeCredits += (aPerformance.audience / 5);
        }

        return volumeCredits;
    }

    private int amountFor(Performance aPerformance) {
        var result = 0;

        switch (aPerformance.play.type) {
            case "tragedy":
                result = 40000;

                if (aPerformance.audience > 30) {
                    result += 1000 * (aPerformance.audience - 30);
                }

                break;
            case "comedy":
                result = 30000;

                if (aPerformance.audience > 20) {
                    result += 10000 + 500 * (aPerformance.audience - 20);
                }

                result += 300 * aPerformance.audience;
                break;
            default:
                throw new Error("unknown type: ${play.type}");
        }

        return result;
    }
}
