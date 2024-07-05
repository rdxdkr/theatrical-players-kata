package theatricalplays;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {
    private Map<String, Play> plays;

    public String print(Invoice invoice, Map<String, Play> plays) {
        var totalAmount = 0;
        var volumeCredits = 0;
        StringBuilder result = new StringBuilder(String.format("Statement for %s\n", invoice.customer));
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

        this.plays = plays;

        for (var aPerformance : invoice.performances) {
            var thisAmount = amountFor(aPerformance);

            // add volume credits
            volumeCredits += Math.max(aPerformance.audience - 30, 0);
            // add extra credit for every ten comedy attendees
            if ("comedy".equals(playFor(aPerformance).type)) volumeCredits += (aPerformance.audience / 5);

            // print line for this order
            result.append(String.format("  %s: %s (%s seats)\n", playFor(aPerformance).name, format.format(thisAmount / 100), aPerformance.audience));
            totalAmount += thisAmount;
        }
        result.append(String.format("Amount owed is %s\n", format.format(totalAmount / 100)));
        result.append(String.format("You earned %s credits\n", volumeCredits));
        return result.toString();
    }

    private Play playFor(Performance aPerformance) {
        return plays.get(aPerformance.playID);
    }

    private int amountFor(Performance performance) {
        var result = 0;

        switch (playFor(performance).type) {
            case "tragedy":
                result = 40000;

                if (performance.audience > 30) {
                    result += 1000 * (performance.audience - 30);
                }

                break;
            case "comedy":
                result = 30000;

                if (performance.audience > 20) {
                    result += 10000 + 500 * (performance.audience - 20);
                }

                result += 300 * performance.audience;
                break;
            default:
                throw new Error("unknown type: ${play.type}");
        }

        return result;
    }
}
