package com.vicom.mdt;

import org.eclipse.ui.plugin.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MobileDeviceTotoalPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static MobileDeviceTotoalPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public MobileDeviceTotoalPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		Configer.startup();
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		Configer.stop();
	}

	/**
	 * Returns the shared instance.
	 */
	public static MobileDeviceTotoalPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("com.vicom.mdt", path);
	}
}
