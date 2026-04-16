package com.semmtech.jena;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class Activator implements BundleActivator {

    private final Logger logger = Logger.getLogger(Activator.class);

    @Override
    public void start(BundleContext context) throws Exception {
        // The Activator only defines the log4j properties
        // These properties will be overwritten by the core plugin!
        PropertyConfigurator.configure(Activator.class.getClassLoader().getResource(
                "log4j.properties"));
        logger.info("Jena bundle started");
    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }
}
