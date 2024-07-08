package theatricalplays;

import java.util.List;
import java.util.Map;

class StatementData {
    public String customer;
    public List<Performance> performances;
    public int totalVolumeCredits;
    public int totalAmount;

    StatementData(String customer, List<Performance> performances) {
        this.customer = customer;
        this.performances = performances;
    }

    static StatementData createStatementData(Invoice invoice, Map<String, Play> plays) {
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
}
