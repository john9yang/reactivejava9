package rates.adapter;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import model.ExchangeRatesResponse;

import com.google.gson.Gson;

import exceptions.CurrencyNotFoundException;
import exceptions.InternalErrorException;

public class ExchangeRatesAdapter {

	private static final String EXCHANGE_RATE_BASE_END_POINT = "http://data.fixer.io/api/latest?base=%s&access_key=%s";

	public Single<ExchangeRatesResponse> getExchangeRates(final String base) {
		
		return Single.create(new SingleOnSubscribe<ExchangeRatesResponse>() {

			public void subscribe(SingleEmitter<ExchangeRatesResponse> subscriber) {
				
				try {
					String accesskey="cbdf5e8b3e94564febe489e2f25cecd1";
					String endPoint = String.format(EXCHANGE_RATE_BASE_END_POINT, base,accesskey);
		    		URL obj = new URL(endPoint);
		    		
		    		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		    		con.setRequestMethod("GET");

		    		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		    		ExchangeRatesResponse response = readRatesFromResponse(in);
		    		subscriber.onSuccess(response);
		    		
				} catch (Exception e) {
					
					subscriber.onError(new InternalErrorException());
				}
			}
		});		
	}	

	public ExchangeRatesResponse readRatesFromResponse(BufferedReader in) throws Exception {
		
		String inputLine;
		StringBuffer response = new StringBuffer();

		try {
			while ((inputLine = in.readLine()) != null) {
    			response.append(inputLine);
    		}
    		in.close();
    		
    		String responseString = response.toString();
    		Gson gson = new Gson();
    		return gson.fromJson(responseString, ExchangeRatesResponse.class);
		} catch (Exception e) {
			throw new CurrencyNotFoundException();
		} finally {
    		in.close();
		} 
	}
}
