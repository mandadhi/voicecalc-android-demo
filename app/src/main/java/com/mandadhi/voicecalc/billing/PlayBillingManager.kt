package com.mandadhi.voicecalc.billing

import android.app.Activity
import com.android.billingclient.api.*

class PlayBillingManager(private val activity: Activity, private val listener: PurchasesUpdatedListener) {
    private val billingClient: BillingClient = BillingClient.newBuilder(activity)
        .setListener(listener)
        .enablePendingPurchases()
        .build()

    fun startConnection() {
        billingClient.startConnection(object: BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {}
            override fun onBillingServiceDisconnected() {}
        })
    }

    fun querySku(skuId: String, callback: (SkuDetails?) -> Unit) {
        val params = SkuDetailsParams.newBuilder().setSkusList(listOf(skuId)).setType(BillingClient.SkuType.INAPP).build()
        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            if (!skuDetailsList.isNullOrEmpty()) callback(skuDetailsList[0]) else callback(null)
        }
    }

    fun launchPurchase(skuDetails: SkuDetails) {
        val flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build()
        billingClient.launchBillingFlow(activity, flowParams)
    }
}
