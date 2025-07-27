package bg.sofia.uni.fmi.mjt.project.models.database.user;

public enum Currency {
    BGN("BGN"),
    EUR("EUR"),
    USD("USD"),
    GBP("GBP"),
    JPY("JPY"),
    CAD("CAD"),
    CHF("CHF"),
    TRY("TRY");

    private final String currency;

    public String getCurrency() {
        return currency;
    }

    Currency(String currency) {
        this.currency = currency;
    }
}
