package com.corbel.pierre.jb.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TableRow;

import com.corbel.pierre.jb.R;
import com.corbel.pierre.jb.lib.AutoResizeTextView;
import com.corbel.pierre.jb.lib.Helper;
import com.corbel.pierre.jb.util.IabHelper;
import com.corbel.pierre.jb.util.IabResult;
import com.corbel.pierre.jb.util.Inventory;
import com.corbel.pierre.jb.util.Purchase;
import com.corbel.pierre.jb.view.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.corbel.pierre.jb.lib.Helper.setStatusBarColor;

public class IAPActivity extends Activity {

    @BindView(R.id.buy_card_view)
    CardView buyCardView;
    @BindView(R.id.premium_table_row)
    TableRow premiumTableRow;
    @BindView(R.id.no_ad_table_row)
    TableRow noAdTableRow;
    @BindView(R.id.joker_25_table_row)
    TableRow joker25TableRow;
    @BindView(R.id.joker_50_table_row)
    TableRow joker50TableRow;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.jokers_left_text_view)
    AutoResizeTextView jokersLeftTextView;

    private static final String TAG = "com.corbel.pierre.jb";
    private static String ITEM_SKU;
    private SharedPreferences preferences;
    private IabHelper mHelper;

    private Animation buyCardViewAnimation;
    private Animation premiumTableRowAnimation;
    private Animation noAdTableRowAnimation;
    private Animation joker25TableRowAnimation;
    private Animation joker50TableRowAnimation;
    private Animation fabAnimation;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iap);
        ButterKnife.bind(this);
        setStatusBarColor(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        jokersLeftTextView.setText(getString(R.string.iap_jokers_left, preferences.getInt("JOKER_IN_STOCK", 5)));

        // Init Animations
        buyCardViewAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        premiumTableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        noAdTableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        joker25TableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        joker50TableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_in);

        // Anim In
        buyCardViewAnimation.setStartOffset(0);
        buyCardView.setVisibility(View.VISIBLE);
        buyCardView.startAnimation(buyCardViewAnimation);

        premiumTableRowAnimation.setStartOffset(100);
        premiumTableRow.setVisibility(View.VISIBLE);
        premiumTableRow.startAnimation(premiumTableRowAnimation);

        noAdTableRowAnimation.setStartOffset(200);
        noAdTableRow.setVisibility(View.VISIBLE);
        noAdTableRow.startAnimation(noAdTableRowAnimation);

        joker25TableRowAnimation.setStartOffset(300);
        joker25TableRow.setVisibility(View.VISIBLE);
        joker25TableRow.startAnimation(joker25TableRowAnimation);

        joker50TableRowAnimation.setStartOffset(400);
        joker50TableRow.setVisibility(View.VISIBLE);
        joker50TableRow.startAnimation(joker50TableRowAnimation);

        fabAnimation.setStartOffset(500);
        fab.setVisibility(View.VISIBLE);
        fab.startAnimation(fabAnimation);

        // IAP
        String base64EncodedPublicKey = getString(R.string.license_key);
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " + result);
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");
                }
            }
        });
    }

    @OnClick(R.id.no_ad_table_row)
    public void buyNoAd() {
        ITEM_SKU = getString(R.string.noads);
        try {
            mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001, mPurchaseFinishedListener, "noAds");
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.joker_25_table_row)
    public void buyLittleHeart() {
        ITEM_SKU = getString(R.string.littleheart);
        try {
            mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001, mPurchaseFinishedListener, "littleHeart");
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.joker_50_table_row)
    public void buyBigHeart() {
        ITEM_SKU = getString(R.string.bigheart);
        try {
            mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001, mPurchaseFinishedListener, "bigHeart");
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.premium_table_row)
    public void buyPremium() {
        ITEM_SKU = getString(R.string.premium);
        try {
            mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001, mPurchaseFinishedListener, "premium");
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            SharedPreferences.Editor mEditor = preferences.edit();
            if (result.isSuccess() && ITEM_SKU.equals(getString(R.string.noads))) {
                mEditor.putBoolean("AD_ENABLED", false);
                mEditor.apply();
            } else if (result.isSuccess() && ITEM_SKU.equals(getString(R.string.littleheart))) {
                int joker = preferences.getInt("JOKER_IN_STOCK", 0) + 25;
                mEditor.putInt("JOKER_IN_STOCK", joker);
                mEditor.apply();
                jokersLeftTextView.setText(getString(R.string.iap_jokers_left, joker));
            } else if (result.isSuccess() && ITEM_SKU.equals(getString(R.string.bigheart))) {
                int joker = preferences.getInt("JOKER_IN_STOCK", 0) + 50;
                mEditor.putInt("JOKER_IN_STOCK", joker);
                mEditor.apply();
                jokersLeftTextView.setText(getString(R.string.iap_jokers_left, joker));
            } else if (result.isSuccess() && ITEM_SKU.equals(getString(R.string.premium))) {
                mEditor.putBoolean("PREMIUM_ENABLED", true);
                mEditor.putBoolean("AD_ENABLED", false);
                mEditor.apply();
            } else {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.internet_failed), Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    };


    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.internet_failed), Snackbar.LENGTH_LONG)
                        .show();
            } else {
                try {
                    mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.internet_failed), Snackbar.LENGTH_LONG)
                        .show();
                return;
            } else if (purchase.getSku().equals(ITEM_SKU)) {
                consumeItem();
            }

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            try {
                mHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        }
        mHelper = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void consumeItem() {
        try {
            mHelper.queryInventoryAsync(mReceivedInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    public void animateOutTo(final Class toActivity) {

        // Init Animations
        buyCardViewAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        premiumTableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        noAdTableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        joker25TableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        joker50TableRowAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);
        fabAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_out);

        // Anim out
        buyCardViewAnimation.setStartOffset(0);
        buyCardView.setVisibility(View.INVISIBLE);
        buyCardView.startAnimation(buyCardViewAnimation);

        premiumTableRowAnimation.setStartOffset(100);
        premiumTableRow.setVisibility(View.INVISIBLE);
        premiumTableRow.startAnimation(premiumTableRowAnimation);

        noAdTableRowAnimation.setStartOffset(200);
        noAdTableRow.setVisibility(View.INVISIBLE);
        noAdTableRow.startAnimation(noAdTableRowAnimation);

        joker25TableRowAnimation.setStartOffset(300);
        joker25TableRow.setVisibility(View.INVISIBLE);
        joker25TableRow.startAnimation(joker25TableRowAnimation);

        joker50TableRowAnimation.setStartOffset(400);
        joker50TableRow.setVisibility(View.INVISIBLE);
        joker50TableRow.startAnimation(joker50TableRowAnimation);

        fabAnimation.setStartOffset(500);
        fab.setVisibility(View.INVISIBLE);
        fab.startAnimation(fabAnimation);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Helper.switchActivity(IAPActivity.this, toActivity, R.anim.fake_anim, R.anim.fake_anim);
            }
        }, 700);
    }

    @Override
    public void onBackPressed() {
        animateOutTo(HomeActivity.class);
    }

    @OnClick(R.id.fab)
    public void home() {
        animateOutTo(HomeActivity.class);
    }
}