package com.adallom.spring.loadbalancer;

import com.adallom.spring.loadbalancer.settings.LoadBalancerSettingsProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class LoadbalancerApplication implements CommandLineRunner
{
	// region Dependencies

	@Autowired
	private LoadBalancerSettingsProvider settingsProvider;

	// endregion

	public static void main(String[] args)
	{
		SpringApplication.run(LoadbalancerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception
	{
		log.info("Load Balancer settings -> ({})", settingsProvider.get());
	}
}
