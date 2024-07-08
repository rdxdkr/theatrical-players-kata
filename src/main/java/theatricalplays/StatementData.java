package theatricalplays;

import java.util.List;

class StatementData {
    public String customer;
    public List<Performance> performances;

    StatementData(String customer, List<Performance> performances) {
        this.customer = customer;
        this.performances = performances;
    }
}
