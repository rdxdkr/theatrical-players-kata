package theatricalplays;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {
    public String print(Invoice invoice, Map<String, Play> plays) {
        var statementData = StatementData.createStatementData(invoice, plays);
        return renderPlainText(statementData);
    }

    private String renderPlainText(StatementData data) {
        StringBuilder result = new StringBuilder(String.format("Statement for %s\n", data.customer));

        for (var aPerformance : data.performances) {
            // print line for this order
            result.append(String.format("  %s: %s (%s seats)\n", aPerformance.play.name, usd(aPerformance.amount / 100), aPerformance.audience));
        }

        result.append(String.format("Amount owed is %s\n", usd(data.totalAmount / 100)));
        result.append(String.format("You earned %s credits\n", data.totalVolumeCredits));
        return result.toString();
    }

    private String usd(int aNumber) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(aNumber);
    }
}
