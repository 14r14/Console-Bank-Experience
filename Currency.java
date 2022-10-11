import java.util.HashMap;

public class Currency {
    public static final double US_TO_GB = 0.77;
    public static final double US_TO_AU = 1.35;
    public static final double US_TO_CA = 1.31;
    public static final double US_TO_NZ = 1.43;
    public static final double US_TO_ZA = 14.47;
    public static final double US_TO_IN = 73.13;
    public static final double US_TO_RU = 63.92;
    public static final double US_TO_CH = 0.91;

    public static final String[] LOCALES = {
        "en-GB", "en-AU", "en-CA", "en-NZ", "en-ZA", "en-IN", "en-RU", "en-CH"
    };

    private static final HashMap<String, String> CURRENCY_SYMBOLS=new HashMap<String,String>(){{put("en-GB","£");put("en-AU","$");put("en-CA","$");put("en-NZ","$");put("en-ZA","R");put("en-IN","₹");put("en-RU","₽");put("en-CH","Fr");}};

    public static double getLocaleCorrespondingExchangeRate(String localeString) {
        switch (localeString) {
            case "en-GB":
                return US_TO_GB;
            case "en-AU":
                return US_TO_AU;
            case "en-CA":
                return US_TO_CA;
            case "en-NZ":
                return US_TO_NZ;
            case "en-ZA":
                return US_TO_ZA;
            case "en-IN":
                return US_TO_IN;
            case "en-RU":
                return US_TO_RU;
            case "en-CH":
                return US_TO_CH;
            default:
                return 1.0;
        }
    }

    public static String getCurrencySymbol(String localeString) {
        return CURRENCY_SYMBOLS.get(localeString);
    }

    public static double convert(String locale, double amount) {
        return amount * getLocaleCorrespondingExchangeRate(locale);
    }
}
