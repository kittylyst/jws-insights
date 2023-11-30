/* Copyright (C) Red Hat 2023 */
package com.redhat.jws.insights;

import com.redhat.insights.InsightsException;
import com.redhat.insights.config.EnvAndSysPropsInsightsConfiguration;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.time.Duration;
import java.util.Optional;

public class TomcatInsightsConfiguration extends EnvAndSysPropsInsightsConfiguration {

    private static final Log log = LogFactory.getLog(TomcatInsightsConfiguration.class);

    // Tomcat style intropection configuration, in addition to EnvAndSysPropsInsightsConfiguration

    private String identificationName = null;

    @Override
    public String getIdentificationName() {
        if (identificationName != null) {
            return identificationName;
        }
        try {
            return super.getIdentificationName();
        } catch (InsightsException e) {
            return "JWS";
        }
    }

    public void setIdentificationName(String identificationName) {
        this.identificationName = identificationName;
    }

    private String machineIdFilePath = null;

    @Override
    public String getMachineIdFilePath() {
        if (machineIdFilePath != null) {
            return machineIdFilePath;
        }
        return super.getMachineIdFilePath();
    }

    public void setMachineIdFilePath(String machineIdFilePath) {
        this.machineIdFilePath = machineIdFilePath;
    }

    private String archiveUploadDir = null;

    @Override
    public String getArchiveUploadDir() {
        if (archiveUploadDir != null) {
            return archiveUploadDir;
        }
        return super.getArchiveUploadDir();
    }

    public void setArchiveUploadDir(String archiveUploadDir) {
        this.archiveUploadDir = archiveUploadDir;
    }

    private String certFilePath = null;

    @Override
    public String getCertFilePath() {
        if (certFilePath != null) {
            return certFilePath;
        }
        return super.getCertFilePath();
    }

    public void setCertFilePath(String certFilePath) {
        this.certFilePath = certFilePath;
    }

    private String certHelperBinary = null;

    @Override
    public String getCertHelperBinary() {
        if (certHelperBinary != null) {
            return certHelperBinary;
        }
        return super.getCertHelperBinary();
    }

    public void setCertHelperBinary(String certHelperBinary) {
        this.certHelperBinary = certHelperBinary;
    }

    private Duration connectPeriod = null;

    @Override
    public Duration getConnectPeriod() {
        if (connectPeriod != null) {
            return connectPeriod;
        }
        return super.getConnectPeriod();
    }

    public String getConnectPeriodValue() {
        return getConnectPeriod().toString();
    }

    public void setConnectPeriodValue(String connectPeriod) {
        try {
            this.connectPeriod = Duration.parse(connectPeriod);
        } catch (Exception e) {
            log.warn("Invalid argument connectPeriod", e);
        }
    }

    private Double httpClientRetryBackoffFactor = null;

    @Override
    public double getHttpClientRetryBackoffFactor() {
        if (httpClientRetryBackoffFactor != null) {
            return httpClientRetryBackoffFactor;
        }
        return super.getHttpClientRetryBackoffFactor();
    }

    // Use a separate fake property since Tomcat does not support double directly
    public String getHttpClientRetryBackoffFactorValue() {
        return Double.toString(getHttpClientRetryBackoffFactor());
    }

    public void setHttpClientRetryBackoffFactorValue(String httpClientRetryBackoffFactor) {
        try {
            this.httpClientRetryBackoffFactor = Double.parseDouble(httpClientRetryBackoffFactor);
        } catch (Exception e) {
            log.warn("Invalid argument httpClientRetryBackoffFactor", e);
        }
    }

    private Long httpClientRetryInitialDelay = null;

    @Override
    public long getHttpClientRetryInitialDelay() {
        if (httpClientRetryInitialDelay != null) {
            return httpClientRetryInitialDelay.longValue();
        }
        return super.getHttpClientRetryInitialDelay();
    }

    public void setHttpClientRetryInitialDelay(long httpClientRetryInitialDelay) {
        this.httpClientRetryInitialDelay = Long.valueOf(httpClientRetryInitialDelay);
    }

    private Integer httpClientRetryMaxAttempts = null;

    @Override
    public int getHttpClientRetryMaxAttempts() {
        if (httpClientRetryMaxAttempts != null) {
            return httpClientRetryMaxAttempts.intValue();
        }
        return super.getHttpClientRetryMaxAttempts();
    }

    public void setHttpClientRetryMaxAttempts(int httpClientRetryMaxAttempts) {
        this.httpClientRetryMaxAttempts = Integer.valueOf(httpClientRetryMaxAttempts);
    }

    private Duration httpClientTimeout = null;

    @Override
    public Duration getHttpClientTimeout() {
        if (httpClientTimeout != null) {
            return httpClientTimeout;
        }
        return super.getHttpClientTimeout();
    }

    public String getHttpClientTimeoutValue() {
        return getHttpClientTimeout().toString();
    }

    public void setHttpClientTimeoutValue(String httpClientTimeout) {
        try {
            this.httpClientTimeout = Duration.parse(httpClientTimeout);
        } catch (Exception e) {
            log.warn("Invalid argument httpClientTimeout", e);
        }
    }

    private String keyFilePath = null;

    @Override
    public String getKeyFilePath() {
        if (keyFilePath != null) {
            return keyFilePath;
        }
        return super.getKeyFilePath();
    }

    public void setKeyFilePath(String keyFilePath) {
        this.keyFilePath = keyFilePath;
    }

    private String maybeAuthToken = null;

    @Override
    public Optional<String> getMaybeAuthToken() {
        if (maybeAuthToken != null) {
            return Optional.of(maybeAuthToken);
        }
        return super.getMaybeAuthToken();
    }

    public String getMaybeAuthTokenValue() {
        return getMaybeAuthToken().orElse(null);
    }

    public void setMaybeAuthTokenValue(String maybeAuthToken) {
        this.maybeAuthToken = maybeAuthToken;
    }

    private String proxyHost = null;
    private Integer proxyPort = null;

    @Override
    public Optional<ProxyConfiguration> getProxyConfiguration() {
        if (proxyHost != null && proxyPort != null) {
            return Optional.of(new ProxyConfiguration(proxyHost, Integer.valueOf(proxyPort)));
        }
        return super.getProxyConfiguration();
    }

    public String getProxyHost() {
        if (proxyHost != null) {
            return proxyHost;
        }
        ProxyConfiguration config = getProxyConfiguration().orElse(null);
        if (config != null) {
            return config.getHost();
        } else {
            return null;
        }
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        if (proxyPort != null) {
            return proxyPort.intValue();
        }
        ProxyConfiguration config = getProxyConfiguration().orElse(null);
        if (config != null) {
            return config.getPort();
        } else {
            return -1;
        }
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = Integer.valueOf(proxyPort);
    }

    private Duration updatePeriod = null;

    @Override
    public Duration getUpdatePeriod() {
        if (updatePeriod != null) {
            return updatePeriod;
        }
        return super.getUpdatePeriod();
    }

    public String getUpdatePeriodValue() {
        return getUpdatePeriod().toString();
    }

    public void setUpdatePeriod(String updatePeriod) {
        try {
            this.updatePeriod = Duration.parse(updatePeriod);
        } catch (Exception e) {
            log.warn("Invalid argument updatePeriod", e);
        }
    }

    private String uploadBaseURL = null;

    @Override
    public String getUploadBaseURL() {
        if (uploadBaseURL != null) {
            return uploadBaseURL;
        }
        return super.getUploadBaseURL();
    }

    public void setUploadBaseURL(String uploadBaseURL) {
        this.uploadBaseURL = uploadBaseURL;
    }

    private String uploadUri = null;

    @Override
    public String getUploadUri() {
        if (uploadUri != null) {
            return uploadUri;
        }
        return super.getUploadUri();
    }

    public void setUploadUri(String uploadUri) {
        this.uploadUri = uploadUri;
    }

    private Boolean optingOut = null;

    @Override
    public boolean isOptingOut() {
        if (optingOut != null) {
            return optingOut.booleanValue();
        }
        return super.isOptingOut();
    }

    public void setOptingOut(boolean optingOut) {
        this.optingOut = Boolean.valueOf(optingOut);
    }

}
