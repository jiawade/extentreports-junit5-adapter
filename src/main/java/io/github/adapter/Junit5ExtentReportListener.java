package io.github.adapter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.observer.ExtentObserver;
import com.aventstack.extentreports.reporter.ReporterConfigurable;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import org.junit.jupiter.api.extension.*;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(Junit5ExtentReportListener.class)
public class Junit5ExtentReportListener implements TestWatcher, BeforeAllCallback, AfterAllCallback {
    protected static ExtentTest test;
    protected static ExtentReports extent;
    protected static ExtentSparkReporter spark;
    protected String defaultOutput = "target/results/report.html";
    private static Map<Class<?>, ExtentTest> executed = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void beforeAll(ExtensionContext context) {
        createReport();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        if (!Objects.isNull(extent)) {
            extent.flush();
        }
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {

    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        if (!Objects.isNull(extent)) {
            Class<?> className = context.getTestClass().get();
            String methodName = context.getDisplayName().replace("(", "").replace(")", "");
            if (!executed.containsKey(className)) {
                test = extent.createTest(className.getSimpleName());
                executed.put(className, test);
            }
            executed.get(className).createNode(methodName).assignCategory(className.getSimpleName());
        }
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        if (!Objects.isNull(extent)) {
            Class<?> className = context.getTestClass().get();
            String methodName = context.getDisplayName().replace("(", "").replace(")", "");
            if (!executed.containsKey(className)) {
                test = extent.createTest(className.getSimpleName());
                executed.put(className, test);
            }
            executed.get(className).createNode(methodName).assignCategory(className.getSimpleName()).log(Status.SKIP, cause);
        }
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        if (!Objects.isNull(extent)) {
            Class<?> className = context.getTestClass().get();
            String methodName = context.getDisplayName().replace("(", "").replace(")", "");
            if (!executed.containsKey(className)) {
                test = extent.createTest(className.getSimpleName());
                executed.put(className, test);
            }
            executed.get(className).createNode(methodName).assignCategory(className.getSimpleName()).log(Status.FAIL, cause);
        }
    }

    private void createReport() {
        ClassLoader loader = Junit5ExtentReportListener.class.getClassLoader();
        String[] DEFAULT_SETUP_PATH = new String[]{
                "extent.properties",
                "com/aventstack/adapter/extent.properties"
        };
        Optional<InputStream> is = Arrays.stream(DEFAULT_SETUP_PATH)
                .map(loader::getResourceAsStream)
                .filter(Objects::nonNull)
                .findFirst();
        if (is.isPresent()) {
            Properties properties = new Properties();
            try {
                properties.load(is.get());
                if (properties.containsKey("extent.reporter.spark.start") && "true".equals(String.valueOf(properties.get("extent.reporter.spark.start")))) {
                    initSpark(properties);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initSpark(Properties properties) {
        if (Objects.isNull(extent)) {
            extent = new ExtentReports();
        }
        String outPath = getOutputPath(properties, "extent.reporter.spark.out");
        if (Objects.isNull(spark)) {
            spark = new ExtentSparkReporter(outPath);
        }
        attch(spark, properties, "extent.reporter.spark.config");
        setOrder(properties, "extent.reporter.spark.viewConfigurer.viewOrder");
        setOfflineMode(properties, "extent.reporter.spark.offlineMode");
    }

    private void setOfflineMode(Properties properties, String key) {
        boolean out = true;
        if (properties != null && properties.get(key) != null)
            out = Boolean.parseBoolean((String) properties.get(key));
        if (out) {
            spark.config().setOfflineMode(out);
        }
    }

    private void setOrder(Properties properties, String key) {
        String out = "";
        if (properties != null && properties.get(key) != null)
            out = String.valueOf(properties.get(key));
        if (!"".equals(out)) {
            spark.viewConfigurer().viewOrder().as(orderConverter(out)).apply();
        }
    }

    private List<ViewName> orderConverter(String order) {
        String[] orders = order.split(",");
        List<String> all = Stream.of(ViewName.AUTHOR, ViewName.CATEGORY, ViewName.EXCEPTION, ViewName.DASHBOARD, ViewName.DEVICE, ViewName.LOG, ViewName.TEST)
                .map(ViewName::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        List<ViewName> matchOders = new ArrayList<>();
        for (String i : orders) {
            if (all.contains(i)) {
                matchOders.add(ViewName.valueOf(i.toUpperCase()));
            }
        }
        return matchOders;
    }

    private String getOutputPath(Properties properties, String key) {
        String out = "";
        if (properties != null && properties.get(key) != null)
            out = String.valueOf(properties.get(key));
        if ("".equals(out)) {
            out = defaultOutput;
        }
        return out;
    }

    private void attch(ReporterConfigurable r, Properties properties, String configKey) {
        Object configPath = properties == null
                ? System.getProperty(configKey)
                : properties.get(configKey);
        if (configPath != null && !String.valueOf(configPath).isEmpty())
            try {
                r.loadXMLConfig(String.valueOf(configPath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        extent.attachReporter((ExtentObserver<?>) r);
    }
}
