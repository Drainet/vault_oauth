package tech.vault.oauth.android

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import net.openid.appauth.*
import net.openid.appauth.browser.AnyBrowserMatcher
import net.openid.appauth.connectivity.DefaultConnectionBuilder

@SuppressLint("StaticFieldLeak")
class VaultSDK private constructor(
        private val appContext: Context
) {

    sealed class AuthResult {
        data class Success(val token: String) : AuthResult()
        data class Failure(val error: Throwable) : AuthResult()
    }

    private val clientId = appContext.getString(appContext.resources.getIdentifier("vault_client_id", null, null))
    private val redirectUri: Uri = Uri.parse("tech.vault.oauth.android.$clientId:/oauth2redirect")
    private val authUri = Uri.parse("https://mithvault.io/oauth/authorize")
    private val tokenUri = Uri.parse("https://mithvault.io/oauth/token")

    companion object {

        private var instance: VaultSDK? = null

        val sharedInstance: VaultSDK
            get() {
                return instance ?: error("try use sdk instance before configure")
            }

        fun configure(context: Context) {
            instance = VaultSDK(context)
        }
    }

    private val authorizationServiceConfiguration = AuthorizationServiceConfiguration(authUri, tokenUri)

    private val appAuthConfiguration: AppAuthConfiguration =
            AppAuthConfiguration.Builder()
                    .setBrowserMatcher(AnyBrowserMatcher.INSTANCE)
                    .setConnectionBuilder(DefaultConnectionBuilder.INSTANCE)
                    .build()

    private val authorizationService = AuthorizationService(appContext, appAuthConfiguration)

    private val authRequest = AuthorizationRequest.Builder(
            authorizationServiceConfiguration,
            clientId,
            ResponseTypeValues.CODE,
            redirectUri
    ).build()

    private val customTabIntent = authorizationService.createCustomTabsIntentBuilder(authRequest.toUri())

    fun requestToken(activity: Activity) {
        activity.startActivity(Intent(activity, VaultOAuthActivity::class.java))
    }

}