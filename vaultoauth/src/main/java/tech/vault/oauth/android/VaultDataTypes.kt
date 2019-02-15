package tech.vault.oauth.android

import com.google.gson.annotations.SerializedName
import java.util.*

data class VaultUserInfo(
        @SerializedName("kyc_level") val kycLevel: Int,
        @SerializedName("stake_level") val stakeLevel: Int,
        @SerializedName("balance") val balance: Double,
        @SerializedName("amount") val amount: Double,
        @SerializedName("staked_amount") val stakedAmount: Double
)

data class RequestTokenBody(
        @SerializedName("client_id")
        val clientId: String,
        val timestamp: String,
        val nonce: Int,
        val state: String,
        @SerializedName("grant_code")
        val grantCode: String
) {
    fun toSig(key: String): String {
        return VaultPayload.build {
            dict(
                    "client_id" to value(clientId),
                    "timestamp" to value(timestamp),
                    "nonce" to value(nonce),
                    "state" to value(state),
                    "grant_code" to value(grantCode)
            )
        }.toSignature(key)
    }
}

data class Balance(
        val currency: String,
        val balance: Double,
        @SerializedName("updated_at")
        val updatedAt: Date
)