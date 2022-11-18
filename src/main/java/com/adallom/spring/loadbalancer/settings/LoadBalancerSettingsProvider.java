package com.adallom.spring.loadbalancer.settings;

import com.adallom.spring.loadbalancer.model.LoadBalancerSettings;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class LoadBalancerSettingsProvider
{
    // region Fields

    private LoadBalancerSettings loadBalancerSettings;

    // endregion

    @Autowired
    public LoadBalancerSettingsProvider(ApplicationArguments applicationArguments, ObjectMapper jacksonObjectMapper) throws IOException
    {
        var settingsInputFilePath = applicationArguments.getSourceArgs()[0];
        log.info("[Service-Settings] Loading formal Load Balancer settings. (Input: {}) ->", settingsInputFilePath);

        var settingsInputFile = new File(settingsInputFilePath);

        if (!settingsInputFile.exists())
        {
            log.error("[Service-Settings] Cannot find the Load Balancer settings file. ({})", settingsInputFilePath);
            throw new IllegalArgumentException(String.format("Cannot find the Load Balancer settings file -> [%s]", settingsInputFilePath));
        }

        log.info("[Service-Settings] Deserializing the Load Balancer settings file. ({})", settingsInputFile);
        this.loadBalancerSettings = jacksonObjectMapper.readValue(settingsInputFile, LoadBalancerSettings.class);

        log.info("[Service-Settings] Loaded settings. (OK) ({})", loadBalancerSettings);
    }

    public LoadBalancerSettings get()
    {
        return loadBalancerSettings;
    }
}
