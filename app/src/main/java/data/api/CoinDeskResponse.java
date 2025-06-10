package data.api;

import com.google.gson.annotations.SerializedName;

public class CoinDeskResponse {
    @SerializedName("bpi")
    private Bpi bpi;

    public Bpi getBpi() {
        return bpi;
    }

    public static class Bpi {
        @SerializedName("USD")
        private Currency usd;

        public Currency getUsd() {
            return usd;
        }
    }

    public static class Currency {
        @SerializedName("rate")
        private String rate;

        public String getRate() {
            return rate;
        }
    }
}
