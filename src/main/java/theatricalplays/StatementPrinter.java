package theatricalplays;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class StatementPrinter {
    private Map<String, Play> plays;

    public String print(Invoice invoice, Map<String, Play> plays) {
        this.plays = plays;

        var statementData = createStatementData(invoice);
        return renderPlainText(statementData);
    }

    private StatementData createStatementData(Invoice invoice) {
        var printer = new StatementPrinter() {
            private Play playFor(Performance aPerformance) {
                return plays.get(aPerformance.playID);
            }

            private int amountFor(Performance aPerformance) {
                return switch (aPerformance.play.type) {
                    case "tragedy" -> {
                        var result = 40000;
                        if (aPerformance.audience > 30) {
                            result += (1000 * (aPerformance.audience - 30));
                        }
                        yield result;
                    }
                    case "comedy" -> {
                        var result = 30000;
                        if (aPerformance.audience > 20) {
                            result += 10000 + (500 * (aPerformance.audience - 20));
                        }
                        yield result + (300 * aPerformance.audience);
                    }
                    default -> throw new Error("unknown type: ${play.type}");
                };
            }

            private int volumeCreditsFor(Performance aPerformance) {
                var volumeCredits = Math.max(aPerformance.audience - 30, 0);
                if ("comedy".equals(aPerformance.play.type)) {
                    volumeCredits += (aPerformance.audience / 5);
                }
                return volumeCredits;
            }

            int totalVolumeCredits(StatementData data) {
                var volumeCredits = 0;
                for (var aPerformance : data.performances) {
                    volumeCredits += aPerformance.volumeCredits;
                }
                return volumeCredits;
            }

            int totalAmount(StatementData data) {
                var totalAmount = 0;
                for (var aPerformance : data.performances) {
                    totalAmount += aPerformance.amount;
                }
                return totalAmount;
            }

            Performance enrichPerformance(Performance aPerformance) {
                var result = new Performance(aPerformance.playID, aPerformance.audience);

                result.play = playFor(result);
                result.amount = amountFor(result);
                result.volumeCredits = volumeCreditsFor(result);
                return result;
            }
        };
        var statementData = new StatementData(
                invoice.customer,
                invoice.performances.stream().map(printer::enrichPerformance).toList()
        );

        statementData.totalVolumeCredits = printer.totalVolumeCredits(statementData);
        statementData.totalAmount = printer.totalAmount(statementData);
        return statementData;
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
