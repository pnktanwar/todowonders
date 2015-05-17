package info.todowonders.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import info.todowonders.R;

public class SearchResultsActivity extends Activity {

	private TextView txtQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);

		// get the action bar
		ActionBar actionBar = getActionBar();

		// Enabling Back navigation on Action Bar icon
		actionBar.setDisplayHomeAsUpEnabled(true);

		txtQuery = (TextView) findViewById(R.id.txtQuery);

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	/**
	 * Handling intent data
	 */
	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);

			/**
			 * Use this query to display search results like 
			 * 1. Getting the data from SQLite and showing in listview 
			 * 2. Making webrequest and displaying the data 
			 * For now we just display the query only
			 */
            String result = "{\"nextUrl\":\"https://affiliate-api.flipkart.net/affiliate/feeds/trackingId/category/v1:dgv-hfp/CNBDZS8KBPFXJG4W.json?expiresAt=1404452681868&sig=9132787549d576c4c4376de5b10753db\",\"validTill\":1404452681868,\"productInfoList\":[{\"productBaseInfo\":{\"productIdentifier\":{\"productId\":\"EXPD7SHPKGVMYGQK\",\"categoryPaths\":{\"categoryPath\":[[{\"title\":\"Pens & Stationery\"},{\"title\":\"School Supplies\"},{\"title\":\"Examination Pads\"}]]}},\"productAttributes\":{\"title\":\"Solo SB 001 Examination Pads Set of 2, Blue, Yellow\",\"productDescription\":\"<p> Solo office supplies are extensively used by students and business professionals for official as well as personal purposes owing to their wide range of products. The Solo SB 001 Examination Pad proves to be very useful for students during examinations and for business professionals for use during meetings for taking notes.</p> <p> This Solo Examination Pad is equipped with a <strong>Lever Type Clip</strong>, which helps hold the papers firmly while writing on them. This Clip requires a soft force and can be easily accessed by a 6-year old or a 75-year old for a firm grip on the paper. The surface of this Solo Examination Pad is <strong>Smooth and Flat</strong> making it look attractive to compliment your style. Along with this Pad, there is a <strong>Pen Holder </strong>provided so that you can always have a pen handy in case of emergency situations.</p>\",\"imageUrls\":{\"100x100\":[\"http://img5a.flixcart.com/image/examination-pad/g/q/k/solo-sb-001-100x100-imad7ptjhdkfhhnv.jpeg\"],\"125x125\":[\"http://img5a.flixcart.com/image/examination-pad/g/q/k/solo-sb-001-125x125-imad7ptjhdkfhhnv.jpeg\"],\"200x200\":[\"http://img5a.flixcart.com/image/examination-pad/g/q/k/solo-sb-001-200x200-imad7ptjhdkfhhnv.jpeg\"],\"275x275\":[\"http://img5a.flixcart.com/image/examination-pad/g/q/k/solo-sb-001-275x275-imad7ptjhdkfhhnv.jpeg\"],\"400x400\":[\"http://img5a.flixcart.com/image/examination-pad/g/q/k/solo-sb-001-400x400-imad7ptjhdkfhhnv.jpeg\"],\"40x40\":[\"http://img5a.flixcart.com/image/examination-pad/g/q/k/solo-sb-001-40x40-imad7ptjhdkfhhnv.jpeg\"],\"75x75\":[\"http://img5a.flixcart.com/image/examination-pad/g/q/k/solo-sb-001-75x75-imad7ptjhdkfhhnv.jpeg\"],\"unknown\":[\"http://img5a.flixcart.com/image/examination-pad/g/q/k/solo-sb-001-original-imad7ptjhdkfhhnv.jpeg\"]},\"maximumRetailPrice\":{\"amount\":372,\"currency\":\"INR\"},\"sellingPrice\":{\"amount\":353,\"currency\":\"INR\"},\"productUrl\":\"http://www.flipkart.com/solo-sb-001-examination-pads/p/itmd7shr9xgc2cds?pid=EXPD7SHPKGVMYGQK&affid=trackingId\",\"productBrand\":\"Solo\",\"inStock\":true,\"codAvailable\":true,\"emiAvailable\":false,\"discountPercentage\":0,\"cashBack\":null,\"offers\":[{\"title\":\"Flipkart's Exclusive Offer for Students:- Get Rs.150 off on a minimum order of Rs 750 on any product sold by WS retail.\"}]}},\"productShippingBaseInfo\":{\"shippingOptions\":[{\"estimatedDelivery\":3,\"deliveryTimeUnits\":\"DAYS\",\"shippingType\":\"REGULAR\"}]}}],\"lastProductId\":\"CNBDZS8KBPFXJG4W\"}";
			txtQuery.setText("Search Query: " + query);

		}

	}
}
