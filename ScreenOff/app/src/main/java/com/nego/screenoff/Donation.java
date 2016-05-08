package com.nego.screenoff;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nego.screenoff.util.IabHelper;
import com.nego.screenoff.util.IabResult;
import com.nego.screenoff.util.Inventory;
import com.nego.screenoff.util.Purchase;

import java.util.ArrayList;
import java.util.List;

public class Donation extends AppCompatActivity {

    static final String TAG = "NEGO_BILLING";
    static final String SKU_COFFEE = "donation_coffee";
    static final String SKU_ICECREAM = "donation_icecream";
    static final String SKU_BREAKFAST = "donation_breakfast";
    static final String SKU_LUNCH = "donation_lunch";
    static final int RC_REQUEST = 10001;
    IabHelper mHelper;

    private LinearLayout donation_card;
    private ProgressBar billing_loader;
    private CardView card_thanks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        donation_card = (LinearLayout) findViewById(R.id.donation_card);
        billing_loader = (ProgressBar) findViewById(R.id.billing_loader);
        card_thanks = (CardView) findViewById(R.id.action_thanks);

        findViewById(R.id.action_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.action_translate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/tommasoberlose/screen-off")));
            }
        });

        findViewById(R.id.action_dev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=5222811638237897947")));
            }
        });

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkcOxaksXl8zH5b0Pod3S5ZRcE3VsUfIKgf3iSgeqcL5s5TbEOSS03QKXetFuhwmkmRpSdzuAkRyfnVAT4DCWAQh9N3hb4f0AYU1PvIUq85Kb8abs1LKjADVddaN7pe7k+21D1sM92HSmhhF7MrE4HX4zR/+tRfxp5wbqfCjtYhMj+eoZRfNCBwuKI6E/FeqXEggXGV9tcrwpVBw+OZSJBaVrL+l/oXsQVTRanBPaDLDhS1SbpOnJtty/J16wvJf2BqP68PeZew07Af/jEELmNT0dFzpoCo6bqOXAhZCn9qisk4aTbP31z6KwIxa4yr+fzPzJPPKRfM+LCO60gKFHZQIDAQAB";

        // Create the helper, passing it our context and the public key to verify signatures with
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    donation_card.setVisibility(View.GONE);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                ArrayList additionalSkuList = new ArrayList();
                additionalSkuList.add(SKU_COFFEE);
                additionalSkuList.add(SKU_ICECREAM);
                additionalSkuList.add(SKU_BREAKFAST);
                additionalSkuList.add(SKU_LUNCH);
                mHelper.queryInventoryAsync(true, additionalSkuList,
                        mQueryFinishedListener);
            }
        });

    }


    IabHelper.QueryInventoryFinishedListener
            mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, final Inventory inventory)
        {
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                donation_card.setVisibility(View.GONE);
                return;
            }

            billing_loader.setVisibility(View.GONE);
            donation_card.setVisibility(View.VISIBLE);

            final Purchase coffee =
                    inventory.getPurchase(SKU_COFFEE);
            final Purchase icecream =
                    inventory.getPurchase(SKU_ICECREAM);
            final Purchase breakfast =
                    inventory.getPurchase(SKU_BREAKFAST);
            final Purchase lunch =
                    inventory.getPurchase(SKU_LUNCH);

            ((TextView) findViewById(R.id.import_donation_coffee)).setText(inventory.getSkuDetails(SKU_COFFEE).getPrice());
            ((TextView) findViewById(R.id.import_donation_icecream)).setText(inventory.getSkuDetails(SKU_ICECREAM).getPrice());
            ((TextView) findViewById(R.id.import_donation_breakfast)).setText(inventory.getSkuDetails(SKU_BREAKFAST).getPrice());
            ((TextView) findViewById(R.id.import_donation_lunch)).setText(inventory.getSkuDetails(SKU_LUNCH).getPrice());


            // COFFEE
            findViewById(R.id.action_donation_coffee).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!inventory.hasPurchase(SKU_COFFEE)) {
                        mHelper.launchPurchaseFlow(Donation.this,  SKU_COFFEE, RC_REQUEST,
                                mPurchaseFinishedListener, "");
                    } else {
                        mHelper.consumeAsync(coffee, new IabHelper.OnConsumeFinishedListener() {
                            @Override
                            public void onConsumeFinished(Purchase purchase, IabResult result) {
                                mHelper.launchPurchaseFlow(Donation.this, SKU_COFFEE, RC_REQUEST,
                                        mPurchaseFinishedListener, "");
                            }
                        });
                    }
                }
            });

            // ICE CREAM
            findViewById(R.id.action_donation_icecream).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!inventory.hasPurchase(SKU_ICECREAM)) {
                        mHelper.launchPurchaseFlow(Donation.this,  SKU_ICECREAM, RC_REQUEST,
                                mPurchaseFinishedListener, "");
                    } else {
                        mHelper.consumeAsync(icecream, new IabHelper.OnConsumeFinishedListener() {
                            @Override
                            public void onConsumeFinished(Purchase purchase, IabResult result) {
                                mHelper.launchPurchaseFlow(Donation.this, SKU_ICECREAM, RC_REQUEST,
                                        mPurchaseFinishedListener, "");
                            }
                        });
                    }
                }
            });

            // BREAKFAST
            findViewById(R.id.action_donation_breakfast).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!inventory.hasPurchase(SKU_BREAKFAST)) {
                        mHelper.launchPurchaseFlow(Donation.this,  SKU_BREAKFAST, RC_REQUEST,
                                mPurchaseFinishedListener, "");
                    } else {
                        mHelper.consumeAsync(breakfast, new IabHelper.OnConsumeFinishedListener() {
                            @Override
                            public void onConsumeFinished(Purchase purchase, IabResult result) {
                                mHelper.launchPurchaseFlow(Donation.this, SKU_BREAKFAST, RC_REQUEST,
                                        mPurchaseFinishedListener, "");
                            }
                        });
                    }
                }
            });

            // LUNCH
            findViewById(R.id.action_donation_lunch).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!inventory.hasPurchase(SKU_LUNCH)) {
                        mHelper.launchPurchaseFlow(Donation.this,  SKU_LUNCH, RC_REQUEST,
                                mPurchaseFinishedListener, "");
                    } else {
                        mHelper.consumeAsync(lunch, new IabHelper.OnConsumeFinishedListener() {
                            @Override
                            public void onConsumeFinished(Purchase purchase, IabResult result) {
                                mHelper.launchPurchaseFlow(Donation.this, SKU_LUNCH, RC_REQUEST,
                                        mPurchaseFinishedListener, "");
                            }
                        });
                    }
                }
            });


        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }

            card_thanks.setVisibility(View.VISIBLE);

            Log.d(TAG, "Purchase successful.");

            Log.d(TAG, "Purchase is " + purchase.getItemType());
            mHelper.consumeAsync(purchase, null);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    public void complain(String message) {
        Log.e(TAG, "Billing Error: " + message);
        //Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
    }

}
