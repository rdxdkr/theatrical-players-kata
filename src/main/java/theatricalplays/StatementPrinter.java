package theatricalplays;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {
    public String print(Invoice invoice, Map<String, Play> plays) {
        var totalAmount = 0;
        var volumeCredits = 0;
        StringBuilder result = new StringBuilder(String.format("Statement for %s\n", invoice.customer));
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

        for (var performance : invoice.performances) {
            var play = plays.get(performance.playID);
            var thisAmount = amountFor(performance, play);

            // add volume credits
            volumeCredits += Math.max(performance.audience - 30, 0);
            // add extra credit for every ten comedy attendees
            if ("comedy".equals(play.type)) volumeCredits += (performance.audience / 5);

            // print line for this order
            result.append(String.format("  %s: %s (%s seats)\n", play.name, format.format(thisAmount / 100), performance.audience));
            totalAmount += thisAmount;
        }
        result.append(String.format("Amount owed is %s\n", format.format(totalAmount / 100)));
        result.append(String.format("You earned %s credits\n", volumeCredits));
        return result.toString();
    }

    private static int amountFor(Performance performance, Play play) {
        var thisAmount = 0;

        switch (play.type) {
            case "tragedy":
                thisAmount = 40000;

                if (performance.audience > 30) {
                    thisAmount += 1000 * (performance.audience - 30);
                }

                break;
            case "comedy":
                thisAmount = 30000;

                if (performance.audience > 20) {
                    thisAmount += 10000 + 500 * (performance.audience - 20);
                }

                thisAmount += 300 * performance.audience;
                break;
            default:
                throw new Error("unknown type: ${play.type}");
        }

        return thisAmount;
    }
}
