package edu.automatization.jakartaee_maievskyi;

import com.sun.management.OperatingSystemMXBean;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.lang.management.ManagementFactory;

@Path("/task1")
public class MyResource {

    @GET
    @Path("/name")
    @Produces("text/plain")
    public String name() {
        return "Vladyslav Maievskyi";
    }

    @GET
    @Path("/sys")
    @Produces("text/plain; charset=UTF-8")
    public String getSystemInfo() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        int processors = osBean.getAvailableProcessors();
        long totalRam = osBean.getTotalMemorySize() / (1024 * 1024);
        long freeRam = osBean.getFreeMemorySize() / (1024 * 1024);

        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");

        return String.format(
                "Деталі сервера:\nОпераційна система: %s (версія %s)\nКількість ядер CPU: %d\nВільна RAM: %d MB\nВсього RAM: %d MB",
                osName, osVersion, processors, freeRam, totalRam
        );
    }
}