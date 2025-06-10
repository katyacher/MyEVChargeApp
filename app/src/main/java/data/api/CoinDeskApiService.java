package data.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CoinDeskApiService {
    @GET("v1/bpi/currentprice.json")
    Call<CoinDeskResponse> getCurrentBitcoinPrice();
}