package theatricalplays;

import java.util.List;
import java.util.Map;

class StatementData {
    public String customer;
    public Map<String, Play> plays;
    public List<Performance> performances;
    public int totalVolumeCredits;
    public int totalAmount;

    StatementData(Invoice invoice, Map<String, Play> plays) {
        customer = invoice.customer;
        this.plays = plays;
        this.performances = invoice.performances.stream().map(this::enrichPerformance).toList();
        totalVolumeCredits = totalVolumeCredits();
        totalAmount = totalAmount();
    }

    static StatementData createStatementData(Invoice invoice, Map<String, Play> plays) {
        return new StatementData(invoice, plays);
    }

    private Play playFor(Performance aPerformance) {
        return plays.get(aPerformance.playID);
    }

    private static int amountFor(Performance aPerformance) {
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

    private static int volumeCreditsFor(Performance aPerformance) {
        var volumeCredits = Math.max(aPerformance.audience - 30, 0);
        if ("comedy".equals(aPerformance.play.type)) {
            volumeCredits += (aPerformance.audience / 5);
        }
        return volumeCredits;
    }

    private int totalVolumeCredits() {
        var volumeCredits = 0;
        for (var aPerformance : performances) {
            volumeCredits += aPerformance.volumeCredits;
        }
        return volumeCredits;
    }

    private int totalAmount() {
        var totalAmount = 0;
        for (var aPerformance : performances) {
            totalAmount += aPerformance.amount;
        }
        return totalAmount;
    }

    private Performance enrichPerformance(Performance aPerformance) {
        var result = new Performance(aPerformance.playID, aPerformance.audience);

        result.play = playFor(result);
        result.amount = amountFor(result);
        result.volumeCredits = volumeCreditsFor(result);
        return result;
    }
}
