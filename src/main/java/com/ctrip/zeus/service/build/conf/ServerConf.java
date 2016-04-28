package com.ctrip.zeus.service.build.conf;

import com.ctrip.zeus.exceptions.ValidationException;
import com.ctrip.zeus.model.entity.Domain;
import com.ctrip.zeus.model.entity.Group;
import com.ctrip.zeus.model.entity.Slb;
import com.ctrip.zeus.model.entity.VirtualServer;
import com.ctrip.zeus.service.build.ConfService;
import com.ctrip.zeus.util.AssertUtils;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author:xingchaowang
 * @date: 3/8/2015.
 */
@Component("serverConf")
public class ServerConf {
    @Resource
    ConfService confService;
    @Resource
    LocationConf locationConf;

    public static final String SSL_PATH = "/data/nginx/ssl/";
    private static final String ZONENAME = "proxy_zone";

    public String generate(Slb slb, VirtualServer vs, List<Group> groups) throws Exception {
        Long slbId = slb.getId();
        Long vsId = vs.getId();

        ConfWriter confWriter = new ConfWriter(1024, true);
        try {
            Integer.parseInt(vs.getPort());
        } catch (Exception e) {
            throw new ValidationException("virtual server [" + vs.getId() + "] port is illegal!");
        }

        confWriter.writeServerStart();
        confWriter.writeCommand("listen", vs.getPort());
        confWriter.writeCommand("server_name", getServerNames(vs));
        confWriter.writeCommand("ignore_invalid_headers", "off");
        confWriter.writeCommand("proxy_http_version", "1.1");

        if (confService.getEnable("server.proxy.buffer.size", slbId, vsId, null, false)) {
            confWriter.writeCommand("proxy_buffer_size", confService.getStringValue("server.proxy.buffer.size", slbId, vsId, null, "8k"));
            confWriter.writeCommand("proxy_buffers", confService.getStringValue("server.proxy.buffers", slbId, vsId, null, "8 8k"));
            confWriter.writeCommand("proxy_busy_buffers_size", confService.getStringValue("server.proxy.busy.buffers.size", slbId, vsId, null, "8k"));
        }

        if (vs.isSsl()) {
            confWriter.writeCommand("ssl", "on");
            confWriter.writeCommand("ssl_certificate", SSL_PATH + vsId + "/ssl.crt");
            confWriter.writeCommand("ssl_certificate_key", SSL_PATH + vsId + "/ssl.key");
        }

        if (confService.getEnable("server.vs.health.check", slbId, vsId, null, false)) {
            locationConf.writeHealthCheckLocation(confWriter, slbId, vsId);
        }

        confWriter.writeCommand("req_status", ZONENAME);

        //add locations
        for (Group group : groups) {
            locationConf.write(confWriter, slb, vs, group);
        }

        if (confService.getEnable("server.errorPage", slbId, vsId, null, false)) {
            for (int sc = 400; sc <= 425; sc++) {
                locationConf.writeErrorPageLocation(confWriter, sc, slbId, vsId);
            }
            for (int sc = 500; sc <= 510; sc++) {
                locationConf.writeErrorPageLocation(confWriter, sc, slbId, vsId);
            }
        }

        confWriter.writeServerEnd();
        return confWriter.getValue();
    }


    private String getServerNames(VirtualServer vs) throws Exception {
        StringBuilder b = new StringBuilder(128);
        for (Domain domain : vs.getDomains()) {
            b.append(" ").append(domain.getName());
        }
        String res = b.toString();
        AssertUtils.assertNotEquals("", res.trim(), "virtual server [" + vs.getId() + "] domain is null or illegal!");
        return res;
    }

    public void writeDyupsServer(ConfWriter confWriter, Long slbId) throws Exception {
        confWriter.writeCommand("dyups_upstream_conf", "conf/dyupstream.conf");
        confWriter.writeServerStart();
        confWriter.writeCommand("listen", confService.getStringValue("server.dyups.port", slbId, null, null, "8081"));

        locationConf.writeDyupsLocation(confWriter);

        confWriter.writeServerEnd();
    }

    public void writeCheckStatusServer(ConfWriter confWriter, String shmZoneName, Long slbId) throws Exception {
        confWriter.writeServerStart();
        confWriter.writeCommand("listen", confService.getStringValue("server.status.port", slbId, null, null, "10001"));
        confWriter.writeCommand("req_status", shmZoneName);
        locationConf.writeCheckStatusLocations(confWriter);
        confWriter.writeServerEnd();
    }

    public void writeDefaultServers(ConfWriter confWriter) {
        confWriter.writeServerStart();
        confWriter.writeCommand("listen", "*:80 default_server");
        locationConf.writeDefaultLocations(confWriter);
        confWriter.writeServerEnd();

        confWriter.writeServerStart();
        confWriter.writeCommand("listen", "*:443 default_server");
        confWriter.writeCommand("ssl", "on");
        confWriter.writeCommand("ssl_certificate", SSL_PATH + "default/ssl.crt");
        confWriter.writeCommand("ssl_certificate_key", SSL_PATH + "default/ssl.key");
        locationConf.writeDefaultLocations(confWriter);
        confWriter.writeServerEnd();
    }
}
