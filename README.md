# Red Hat Insights for JWS

This project provides a listener for JWS that will upload data to Red Hat Insights.

It currently supports JWS 6 (& so needs Java 11)

`mvn package` to build.

The listener should be added to `server.xml`, such as
```
<Server>
  <Listener className="com.redhat.jws.insights.InsightsLifecycleListener" />
</Server>
```

Configuration is done through the standard `com.redhat.insights.config.EnvAndSysPropsInsightsConfiguration` from Insights, which uses various environment and Java system properties.

Alternatively, attributes matching the properties can be set on the `Listener` element in `server.xml`:

* `identificationName`
* `machineIdFilePath`
* `archiveUploadDir`
* `certFilePath`
* `certHelperBinary`
* `connectPeriodValue`
* `httpClientRetryBackoffFactorValue`
* `httpClientRetryInitialDelay`
* `httpClientRetryMaxAttempts`
* `httpClientTimeoutValue`
* `keyFilePath`
* `maybeAuthTokenValue`
* `proxyHost`
* `proxyPort`
* `updatePeriodValue`
* `uploadBaseURL`
* `uploadUri`
* `optingOut`
