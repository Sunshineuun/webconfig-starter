package com.qiusm.config.starter.cat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author qiushengming
 */
public class InetUtils {
    private static final Logger log = LoggerFactory.getLogger(InetUtils.class);

    public InetUtils() {
    }

    public static InetAddress findFirstNonLoopbackAddress() {
        InetAddress result = null;

        try {
            int lowest = 2147483647;
            Enumeration nics = NetworkInterface.getNetworkInterfaces();

            label54:
            while (true) {
                NetworkInterface ifc;
                while (true) {
                    do {
                        if (!nics.hasMoreElements()) {
                            break label54;
                        }

                        ifc = (NetworkInterface) nics.nextElement();
                    } while (!ifc.isUp());

                    log.trace("Testing interface: " + ifc.getDisplayName());
                    if (ifc.getIndex() >= lowest && result != null) {
                        if (result != null) {
                            continue;
                        }
                        break;
                    }

                    lowest = ifc.getIndex();
                    break;
                }

                Enumeration addrs = ifc.getInetAddresses();

                while (addrs.hasMoreElements()) {
                    InetAddress address = (InetAddress) addrs.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                        log.trace("Found non-loopback interface: " + ifc.getDisplayName());
                        result = address;
                    }
                }
            }
        } catch (IOException var7) {
            log.error("Cannot get first non-loopback address", var7);
        }

        if (result != null) {
            return result;
        } else {
            try {
                return InetAddress.getLocalHost();
            } catch (UnknownHostException var6) {
                log.warn("Unable to retrieve localhost");
                return null;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(findFirstNonLoopbackAddress().getHostName());
    }
}