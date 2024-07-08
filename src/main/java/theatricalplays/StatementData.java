package theatricalplays;

import java.util.List;

class StatementData {
    public String customer;
    public List<Performance> performances;
    public int totalVolumeCredits;
    public int totalAmount;

    StatementData(String customer, List<Performance> performances) {
        this.customer = customer;
        this.performances = performances;
    }
}
